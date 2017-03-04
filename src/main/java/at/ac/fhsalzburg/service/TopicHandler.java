package at.ac.fhsalzburg.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopicHandler {

    private Map<String,Topic> topics = new HashMap<>();
    private Map<String, String> clientToTopic = new HashMap<>();
    private static ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("topics-%d").setDaemon(true).build());


    //hinzufügen von einem client
    public synchronized void addClient(String sessionId, String destination){
        Topic topic = topics.get(destination);
        if(topic != null) {
            if (!clientToTopic.containsKey(sessionId)) {
                clientToTopic.put(sessionId, destination);
                topic.subscribeSession(sessionId);
            }
            if (!topic.isRunning()) {
                this.pool.submit(topic);
            }
        }
        else {
            //inform log about external websocket call
        }
    }

    //entfernen von einem client
    public void removeClient(String sessionId){
        String destination = clientToTopic.remove(sessionId);
        Topic topic;
        if(topics.containsKey(destination)) {
           topic = topics.get(destination);
            topic.unsubscribeSession(sessionId);
            synchronized (topic) {
                if (!topic.isSubscribed())
                    topics.remove(destination);
            }
        }
    }

    //erzeugen eines topic
    public synchronized void createTopic(String destination, MessageSendingOperations<String> messagingTemplate){
        if(!topics.containsKey(destination)){
            Topic topic = new RandomDataTopic(destination, messagingTemplate);
            topics.put(destination,topic);
        }
    }

}
