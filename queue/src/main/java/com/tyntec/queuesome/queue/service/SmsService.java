package com.tyntec.queuesome.queue.service;

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

    @RequestMapping(name = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public String getSms(@RequestParam("From") String from, @RequestParam("To") String to, @RequestParam("Body") String body) {
        log.info("Got message: {} From: {} To: {}", body, from, to);
        switch (body.trim()) {
            case "?":
            case "":
            default:
                List<QueueTicketEntity> queueTicketEntities = qSvc.queryQueue(from);
                QueueTicketEntity queueTicketEntity = null;
                for(QueueTicketEntity q: queueTicketEntities) {
                    if (q.getQueueName().equalsIgnoreCase(to)) {
                        queueTicketEntity = q;
                        break;
                    }
                }
                if(queueTicketEntity == null) {
                    queueTicketEntity = qSvc.enQueue(to, from);
                    return createSmsResponse("You got ticket number " + queueTicketEntity.getNumber());
                } else {
                    QueueEntity queue = qSvc.getQueue(to);
                    return createSmsResponse("Your ticket number is still " + queueTicketEntity.getNumber()
                            + ". There are " + (queue.getQueue().size() -1) + " waiting before you");
                }

        }
    }

    private String createSmsResponse(String text) {
        return "<Response><Sms>" + text + "</Sms></Response>";
    }

}
