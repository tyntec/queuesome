package com.tyntec.queuesome.queue.service;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sms")
@Log4j2
public class SmsService {

    @Autowired
    QueueBackendService qSvc;

    @Autowired
    AIConfiguration configuration;

    @Autowired
    AIDataService dataService;

    @RequestMapping(name = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public String getSms(@RequestParam("From") String from, @RequestParam("To") String to, @RequestParam("Body") String body) throws AIServiceException {
        log.info("Got message: {} From: {} To: {}", body, from, to);
        AIRequest req = new AIRequest(body);
        req.setSessionId(from + to);
        AIResponse aiResponse = dataService.request(req);
        log.info(aiResponse.getResult().getAction());
        log.info(aiResponse);
        List<QueueTicketEntity> queueTicketEntities = qSvc.queryQueue(from);
        QueueTicketEntity queueTicketEntity = null;
        for (QueueTicketEntity q : queueTicketEntities) {
            if (q.getQueueName().equalsIgnoreCase(to)) {
                queueTicketEntity = q;
                break;
            }
        }
        switch (aiResponse.getResult().getAction()) {
            case "cancel_specific_ticket":
            case "cancel_number": {
                if (queueTicketEntity != null) {
                    if (aiResponse.getResult().getParameters().containsKey("number")) {
                        int num = aiResponse.getResult().getParameters().get("number").getAsInt();
                        if (num != queueTicketEntity.getNumber()) {
                            return createSmsResponse("Your ticket number is " + queueTicketEntity.getNumber() + ", not " + num + ".");
                        }
                    }
                    qSvc.removeTicketFromQueue(queueTicketEntity.getQueueName(), queueTicketEntity.getNumber());
                    return createSmsResponse("Thank you. Your ticket number " + queueTicketEntity.getNumber() + " has been cancelled.");
                } else {
                    return createSmsResponse("You don't have any tickets in the queue.");
                }
            }

            case "enqueue":
                if (queueTicketEntity == null) {
                    queueTicketEntity = qSvc.enQueue(to, from);
                    return createSmsResponse("Welcome. Your ticket number is " + queueTicketEntity.getNumber() + ".");
                }

            case "queue_status":
            default:
                QueueEntity queue = qSvc.getQueue(to);
                if (queueTicketEntity != null) {
                    return createSmsResponse("Your ticket number is still " + queueTicketEntity.getNumber()
                            + ". There are " + (queue.getQueue().size() - 1) + " waiting in front of you.");
                } else {
                    return createSmsResponse("Welcome to '" + queue.getDescription() + "' there are currently " + queue.getCurrentSize()
                            + " people waiting in line.");
                }
        }

    }
    
    private String getEstimationText(QueueTicketEntity ticket) {
	int position = qSvc.getTicketPosition(ticket.getQueueName(), ticket.getNumber());
	String estimate = qSvc.estimate(position);
        return " Estimated time remaining: " + estimate;
    }

    private String createSmsResponse(String text) {
        return "<Response><Sms>" + text + "</Sms></Response>";
    }

}
