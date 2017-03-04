package at.ac.fhsalzburg.service;

import org.springframework.messaging.core.MessageSendingOperations;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomDataTopic extends Topic {


    public RandomDataTopic(String name, MessageSendingOperations<String> messagingTemplate) {
        super(name, messagingTemplate);
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
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
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
