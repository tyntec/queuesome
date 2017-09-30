package com.tyntec.queuesome.queue.repository;

import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryQueueBackend implements QueueBackendService{

    Map<String, QueueEntity> queuesIndex = new HashMap<>();
    Map<String, QueueTicketEntity> ticketIndex = new HashMap<>();

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
        QueueEntity q = new QueueEntity(name, description , new ArrayList<>(), new AtomicInteger(0));
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

    private String getTicketKey(String queueName, Integer number) {
        return queueName + "-" + number.toString();
    }

}
