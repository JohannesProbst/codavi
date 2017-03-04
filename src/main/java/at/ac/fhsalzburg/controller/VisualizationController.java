package at.ac.fhsalzburg.controller;

import at.ac.fhsalzburg.repository.UuidRepository;
import at.ac.fhsalzburg.service.TopicHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VisualizationController {

    @Autowired
    UuidRepository uuidRepository;

    @Autowired
    TopicHandler topicHandler;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @RequestMapping(value = "uuids/{uuid}", method = RequestMethod.GET, params = {"data", "interval"})
    public String getVisualization(@PathVariable("uuid") String chartUuid, @RequestParam("data") String data, @RequestParam("interval") String interval, Model model){
        String uuidSpecification = uuidRepository.getUuid(chartUuid);
        model.addAttribute("spec", uuidSpecification);
        model.addAttribute("uuid", chartUuid);

        topicHandler.createTopic("/topic/"+chartUuid, messagingTemplate);

        return "index";
    }

}
