package com.tyntec.queuesome.queue.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueueTicketEntity {

    private String queueName;
    private Integer number;
    private String who;
}
