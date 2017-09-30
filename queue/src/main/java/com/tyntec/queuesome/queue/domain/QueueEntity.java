package com.tyntec.queuesome.queue.domain;

import lombok.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class QueueEntity {
    private String name;
    private String description;
    private List<QueueTicketEntity> queue;
    private AtomicInteger lastAssignedNumber;

    public Integer getCurrentSize() {
        return queue == null ? 0 : queue.size();
    }
}
