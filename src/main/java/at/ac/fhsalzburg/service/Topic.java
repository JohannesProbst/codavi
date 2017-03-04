package at.ac.fhsalzburg.service;

import org.springframework.messaging.core.MessageSendingOperations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Topic implements Runnable {

    private final String name;

    private final MessageSendingOperations<String> messagingTemplate;

    private boolean running = false;


    private Set<String> sessions = Collections.synchronizedSet(new HashSet<>());

    public Topic(String name, MessageSendingOperations<String> messagingTemplate) {
        this.name = name;
        this.messagingTemplate = messagingTemplate;
    }

    protected void setRunning(Boolean value){
        this.running = value;
    }

    public Boolean isRunning(){
        return this.running;
    }

    public Boolean isSubscribed(){
        return !this.sessions.isEmpty();
    }

    public void subscribeSession(final String session){
        sessions.add(session);
    }

    public void unsubscribeSession(final String session){
        sessions.remove(session);
    }

    public void send(String sb){
        messagingTemplate.convertAndSend(name,sb.toString());
    }

}
