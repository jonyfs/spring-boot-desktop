package br.com.jonyfs.message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    @JmsListener(destination = "mailbox")
    public void receiveMessage(Message message) {
        System.out.println("Received <" + message + ">");
    }
}
