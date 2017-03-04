package at.ac.fhsalzburg.configuration;

import at.ac.fhsalzburg.service.TopicHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class ChannelInterceptor extends ChannelInterceptorAdapter {

    private TopicHandler topicHandler;

    public ChannelInterceptor(TopicHandler topicHandler) {
        this.topicHandler = topicHandler;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        if (command.equals(StompCommand.SUBSCRIBE)) {
            //hinzufügen der session zur destination
            topicHandler.addClient(sessionId, destination);
                return message;
        }
        if (command.equals(StompCommand.DISCONNECT)){
            //entfernen der session aus der destination
            topicHandler.removeClient(sessionId);
        }

        //als erstes kommt ein connect command, dann ein subscribe command in dem unter headers im simpDestination "/topic/23a45cd535f" angeführt ist.
        return message;
    }
}