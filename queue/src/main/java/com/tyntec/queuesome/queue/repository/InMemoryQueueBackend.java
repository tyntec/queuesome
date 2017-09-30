package com.tyntec.queuesome.queue.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class InMemoryQueueBackend implements QueueBackendService{

    Map<String, QueueEntity> queuesIndex = new HashMap<>();
    Map<String, QueueTicketEntity> ticketIndex = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();
    File queueIdxFile = new File(System.getProperty("java.io.tmpdir") + "/queueidx.json");
    File ticketIdxFile = new File(System.getProperty("java.io.tmpdir") + "/ticketIndex.json");

    public InMemoryQueueBackend(){
        try {
            if(queueIdxFile.exists() && ticketIdxFile.exists()) {
                TypeReference<HashMap<String,QueueEntity>> qtypeRef
                        = new TypeReference<HashMap<String,QueueEntity>>() {};
                TypeReference<HashMap<String,QueueTicketEntity>> ttypeRef
                        = new TypeReference<HashMap<String,QueueTicketEntity>>() {};
                queuesIndex = mapper.readValue(queueIdxFile, qtypeRef);
                log.info("Loaded: {}", queuesIndex);
                ticketIndex = mapper.readValue(ticketIdxFile, ttypeRef);
                log.info("Loaded: {}", ticketIndex);
            }
        } catch (IOException ex) {
            log.error("Could not load", ex);
        }
    }

    @Override
    public QueueTicketEntity enQueue(String queueName, String who) {
        QueueEntity queueEntity = queuesIndex.get(queueName);
        if(queueEntity == null) {
            throw new IllegalArgumentException("Queue name does not exist");
        }
        QueueTicketEntity ticket = new QueueTicketEntity();
        ticket.setQueueName(queueName);
        ticket.setWho(who);
        ticket.setNumber(queueEntity.getLastAssignedNumber().incrementAndGet());
        queueEntity.getQueue().add(ticket);
        ticketIndex.put(getTicketKey(queueName, ticket.getNumber()), ticket);
        return ticket;
    }

    @Override
    public QueueTicketEntity deQueue(String queueName, Integer ticketNumber) {
        QueueEntity queueEntity = queuesIndex.get(queueName);
        if(queueEntity == null) {
            throw new IllegalArgumentException("Queue name does not exist");
        }
        QueueTicketEntity queueTicketEntity = ticketIndex.remove(getTicketKey(queueName, ticketNumber));
        if(queueTicketEntity == null) {
            throw new IllegalArgumentException("Ticket number does not exist");
        }
        queueEntity.getQueue().remove(queueTicketEntity);
        return queueTicketEntity;
    }

    @Override
    public void removeTicketFromQueue(String queueName, Integer ticketNumber) {
        deQueue(queueName, ticketNumber);
    }

    @Override
    public List<QueueTicketEntity> queryQueue(String who) {
        List<QueueTicketEntity> response = new ArrayList<>();
        ticketIndex.values().stream()
                .filter(queueTicketEntity -> queueTicketEntity.getWho().equalsIgnoreCase(who))
                .forEach(ticket -> response.add(ticket));
        return response;
    }

    @Override
    public QueueEntity createQueue(String name, String description) {
        QueueEntity q = new QueueEntity(name, description , new ArrayList<>(), new AtomicInteger(0), 0);
        queuesIndex.put(name, q);
        return q;
    }

    @Override
    public void deleteQueue(String name) {
        queuesIndex.remove(name);
    }

    @Override
    public List<QueueEntity> getAllQueues() {
        return new ArrayList<>(queuesIndex.values());
    }

    @Override
    public QueueEntity getQueue(String name) {
        return queuesIndex.get(name);
    }

    @Override
    public Integer getTicketPosition(String queueName, Integer ticketNumber) {
        QueueEntity queueEntity = queuesIndex.get(queueName);
        if (queueEntity == null) return 0;
        QueueTicketEntity ne = queueEntity.getQueue().stream()
            .filter(queueTicketEntity -> queueTicketEntity.getNumber() == ticketNumber )
            .findFirst()
            .get();
        return queueEntity.getQueue().indexOf(ne);
    }

    private String getTicketKey(String queueName, Integer number) {
        return queueName + "-" + number.toString();
    }

    @PreDestroy
    private void persist() throws IOException {
        mapper.writeValue(queueIdxFile, queuesIndex);
        mapper.writeValue(ticketIdxFile, ticketIndex);
    }
}
