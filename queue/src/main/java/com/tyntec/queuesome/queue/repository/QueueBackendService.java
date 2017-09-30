package com.tyntec.queuesome.queue.repository;

import com.tyntec.queuesome.queue.domain.QueueEntity;
import com.tyntec.queuesome.queue.domain.QueueTicketEntity;

import java.util.List;

public interface QueueBackendService {

    QueueTicketEntity enQueue(String queueName, String who);

    QueueTicketEntity deQueue(String queueName, Integer ticketNumber);

    void removeTicketFromQueue(String queueName, Integer ticketNumber);

    List<QueueTicketEntity> queryQueue(String who);

    QueueEntity createQueue(String name, String description);

    void deleteQueue(String name);

    List<QueueEntity> getAllQueues();

    Integer getTicketPosition(String queueName, Integer ticketNumber);

    QueueEntity getQueue(String name);

}
