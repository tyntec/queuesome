package com.tyntec.queuesome.queue.service;

import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;
import com.tyntec.queuesome.queue.repository.PassphraseProvider;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getVoice(@RequestParam(name = "From", required = false) String from,
                                           @RequestParam(name = "To", required = false) String to,
                                           @RequestParam(name = "CallSid", required = false) String callSid,
                                           @RequestParam(name = "AccountSid", required = false) String accountSid,
                                           @RequestParam(name = "CallStatus", required = false) String callStatus,
                                           @RequestParam(name = "ApiVersion", required = false) String apiVersion,
                                           @RequestParam(name = "CallerName", required = false) String callerName,
                                           @RequestParam(name = "Direction", required = false) String direction) throws Exception {

        log.info("Got inbound call From: {} To: {}, Direction: {}. Call status: {}", from, to, direction, callStatus);

        if (callStatus.equalsIgnoreCase("initiated")
                || callStatus.equalsIgnoreCase("ringing")
                || callStatus.equalsIgnoreCase("in-progress")) {

            to = to.replaceFirst("\\+", "").trim();
            log.info("Queue name >{}<", to);
            List<QueueTicketEntity> queueTicketEntities = qSvc.queryQueue(from);
            QueueTicketEntity queueTicketEntity = null;
            for (QueueTicketEntity q : queueTicketEntities) {
                if (q.getQueueName().equalsIgnoreCase(to)) {
                    queueTicketEntity = q;
                    break;
                }
            }
            String response = null;
            if (queueTicketEntity == null) {
                queueTicketEntity = qSvc.enQueue(to, from);
                int position = qSvc.getTicketPosition(queueTicketEntity.getQueueName(), queueTicketEntity.getNumber());
                response = createTtsResponse("Your ticket number is " + queueTicketEntity.getNumber() +
                        ". Passphrase " + passphrase.generatePassphrase() + "." + getWaitingPeopleText(position) + getEstimationText(position));
            } else {
                QueueEntity queue = qSvc.getQueue(to);
                response = createTtsResponse("Appointment is already booked. Your ticket number is " + queueTicketEntity.getNumber()
                        + ". " + getWaitingPeopleText(queue.getQueue().size() - 1) + getEstimationText(queue.getQueue().size() - 1));
            }

            log.info("Response: {}", response);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(null);

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

