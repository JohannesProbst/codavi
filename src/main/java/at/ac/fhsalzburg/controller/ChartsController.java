package at.ac.fhsalzburg.controller;

import at.ac.fhsalzburg.repository.PredefinedChartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/charts")
public class ChartsController {

    @Autowired
    PredefinedChartRepository predefinedChartRepository;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String predefinedChartsMenu(){

        String chartJsonSchema = getChartEntriesOverview();

        return chartJsonSchema;
    }

    public String getChartEntriesOverview() {
        return "";
    }

    @RequestMapping(value = "/{chartName}", method = RequestMethod.GET)
    public HttpEntity<ObjectNode> getConfigOfPredefinedChart(@PathVariable("chartName") String chartName) throws IOException {
        String predefinedChartConfig = predefinedChartRepository.getChartByName(chartName);
        ObjectNode node = mapper.readValue(predefinedChartConfig, ObjectNode.class);
        return new ResponseEntity<ObjectNode>(node, HttpStatus.OK);
    }
}
