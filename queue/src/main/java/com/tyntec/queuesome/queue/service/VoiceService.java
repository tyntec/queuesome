package com.tyntec.queuesome.queue.service;

import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;
import com.tyntec.queuesome.queue.repository.PassphraseProvider;
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
@RequestMapping("/voice")
@Log4j2
public class VoiceService {

    @Autowired
    QueueBackendService qSvc;

    @Autowired
    PassphraseProvider passphrase;

    @RequestMapping(name = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public String getVoice(@RequestParam("From") String from, @RequestParam("To") String to, @RequestParam("CallSid") String callSid,
                           @RequestParam("AccountSid") String accountSid, @RequestParam("CallStatus") String callStatus,
                           @RequestParam("ApiVersion") String apiVersion, @RequestParam("CallerName") String callerName,
                           @RequestParam("Direction") String direction) {
        log.info("Got inbound call : {} From: {} To: {}, Direction: {}", from, to, direction);

        List<QueueTicketEntity> queueTicketEntities = qSvc.queryQueue(from);
        QueueTicketEntity queueTicketEntity = null;
        for (QueueTicketEntity q : queueTicketEntities) {
            if (q.getQueueName().equalsIgnoreCase(to)) {
                queueTicketEntity = q;
                break;
            }
        }
        if (queueTicketEntity == null) {
            queueTicketEntity = qSvc.enQueue(to, from);
            int position = qSvc.getTicketPosition(queueTicketEntity.getQueueName(), queueTicketEntity.getNumber());
            return createTtsResponse("Your ticket number is " + queueTicketEntity.getNumber() +
                    ". Passphrase " + passphrase + "." + getWaitingPeopleText(position) + getEstimationText(position));
        } else {
            QueueEntity queue = qSvc.getQueue(to);
            int position = qSvc.getTicketPosition(queueTicketEntity.getQueueName(), queueTicketEntity.getNumber());
            return createTtsResponse("Appointment is already booked. Your ticket number is " + queueTicketEntity.getNumber()
                    + ". " + getWaitingPeopleText(position) + getEstimationText(position));
        }

    }
    
    private String getWaitingPeopleText(int position) {
	if (position == 0)
	    return "No people are waiting in front of you.";
	else
	    if (position == 1)
		return "1 person is waiting in front of you.";
	    else
		return "There are " + position + " people waiting in front of you.";
    }
    
    private String getEstimationText(int position) {
    	String estimate = qSvc.estimate(position);
        return " Estimated time remaining: " + estimate;
    }
    
    private String createTtsResponse(String text) {
        return "<Response><Say>" + text + "</Say></Response>";
    }

}
