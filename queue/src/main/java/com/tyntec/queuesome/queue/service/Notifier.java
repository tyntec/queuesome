package com.tyntec.queuesome.queue.service;

public interface Notifier {

    void sendNotification(String from, String to, String text);
}
