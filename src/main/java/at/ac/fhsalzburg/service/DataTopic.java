package at.ac.fhsalzburg.service;

import at.ac.fhsalzburg.service.schema.DataSchema;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.Random;

/**
 * Created by Johan on 05.03.2017.
 */
public class DataTopic extends Topic {

    private DataSchema schema;
    private String datasource;

    public DataTopic(String name, MessageSendingOperations<String> messagingTemplate, String datasource, DataSchema schema) {
        super(name, messagingTemplate);
        this.datasource = datasource;
        this.schema = schema;
    }

    @Override
    public void run() {
        setRunning(true);
        while(true) {
            Random rnd = new Random();
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            int max = (1 + rnd.nextInt(7));
            for (int i = 0; i < max; i++) {
                sb.append("{ \"x\": ").append(i).append(", \"y\": ").append(rnd.nextInt(100)).append("}");
                if (i + 1 < max) {
                    sb.append(",");
                }
            }
            sb.append("]");
            send(sb.toString());
            try {
                Thread.sleep(this.interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(!this.isSubscribed()){
                setRunning(false);
                return;
            }
        }
    }
}
