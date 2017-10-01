package com.tyntec.queuesome.queue.service;

import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;

@Log4j2
public class RestcommNotifier implements Notifier {

    @Value(value = "${restcomm.url}")
    String restcommUri = "https://tadhack.restcomm.com/restcomm/2012-04-24/Accounts/ACf2f7fb9fb8cf5d735e48351120d36089/SMS/Messages";
    @Value(value = "${restcomm.accountSid}")
    String accountSid;
    @Value(value = "${restcomm.accountToken}")
    String accountToken;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendNotification(String from, String to, String text) {
        log.info("Notify. From: {} To: {} Text: {}", from, to , text);
        String auth = accountSid + ":" + accountToken;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + StringUtils.newStringUtf8(encodedAuth);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restcommUri);

        MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String,String>();
        parameters.add("To", "+" + to);
        parameters.add("From", "+" + from);
        parameters.add("Body",  text);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        // Create the http entity for the request
        HttpEntity<MultiValueMap<String,String>> entity =
                new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

        log.info(entity);
        try {
            ResponseEntity<String> exchange = restTemplate.postForEntity(builder.build().toUri(), entity, String.class);
            log.info(exchange.getStatusCodeValue() + " " + exchange.getBody());
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
        }

    }
}
