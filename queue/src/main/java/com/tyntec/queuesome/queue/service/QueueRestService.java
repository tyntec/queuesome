package com.tyntec.queuesome.queue.service;

import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;
import com.tyntec.queuesome.queue.repository.QueueBackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueRestService {

    @Autowired
    QueueBackendService qSvc;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<QueueEntity>> getAllQueues() {
        return ResponseEntity.ok(qSvc.getAllQueues());
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<QueueEntity> createQueue(@RequestParam(name = "name") String name, @RequestParam("description") String description) {
        return ResponseEntity.ok(qSvc.createQueue(name, description));
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    public ResponseEntity<QueueTicketEntity> createTicket(@PathVariable("name") String queueName,
                                                          @RequestParam("who") String who) {
        return ResponseEntity.ok(qSvc.enQueue(queueName, who));
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ResponseEntity<QueueEntity> getQueue(@PathVariable("name") String queueName) {
        return ResponseEntity.ok(qSvc.getQueue(queueName));
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public void deleteQueue(@PathVariable("name") String queueName) {
        qSvc.deleteQueue(queueName);
    }

    @RequestMapping(value = "/{name}/dequeue", method = RequestMethod.POST)
    public ResponseEntity<QueueTicketEntity> dequeueNextTicket(@PathVariable("name") String queueName,
                                                               @RequestParam("number") Integer number) {
        return ResponseEntity.ok(qSvc.deQueue(queueName, number));
    }

    @RequestMapping(value = "/{name}/cancelTicket", method = RequestMethod.DELETE)
    public void cancelTicket(@PathVariable("name") String queueName,
                             @RequestParam("number") Integer number) {
        qSvc.removeTicketFromQueue(queueName, number);
    }
}
