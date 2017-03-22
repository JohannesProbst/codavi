package at.ac.fhsalzburg.service;

import at.ac.fhsalzburg.service.schema.DataProperty;
import at.ac.fhsalzburg.service.schema.DataObjectSchema;
import at.ac.fhsalzburg.service.schema.VegaSchema;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.messaging.core.MessageSendingOperations;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public synchronized void createTopic(String destination, MessageSendingOperations<String> messagingTemplate, String schema, String dataSource, Long interval){
        if(!topics.containsKey(destination)){

            Topic topic;
            if(!Strings.isNullOrEmpty(schema)) {
                try {
                    // Could be cached via springconfiguration
                    JAXBContext jc = org.eclipse.persistence.jaxb.JAXBContext.newInstance(VegaSchema.class,DataObjectSchema.class, DataProperty.class);
                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    unmarshaller.setProperty("eclipselink.media-type", "application/json");
                    unmarshaller.setProperty("eclipselink.json.include-root", false);
                    InputStream json = new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8));
                    VegaSchema parsedSchema = (VegaSchema) unmarshaller.unmarshal(new StreamSource(json), VegaSchema.class).getValue();
                    topic = new DataTopic(destination, messagingTemplate, dataSource, parsedSchema.getSchema());
                } catch (JAXBException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } else {
                topic = new RandomDataTopic(destination, messagingTemplate);
            }
            topics.put(destination,topic);
        } else {
            Topic topic = topics.get(destination);
            if(topic != null){
                topic.updateInterval(interval);
            }
        }
    }

}
