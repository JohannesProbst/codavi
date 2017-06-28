package at.ac.fhsalzburg.controller;

import at.ac.fhsalzburg.entity.Chart;
import at.ac.fhsalzburg.repository.PredefinedChartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/charts")
public class ChartsController {

    @Autowired
    PredefinedChartRepository predefinedChartRepository;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Chart> predefinedChartsMenu(){

        List<Chart> charts = getPredefinedChartEntriesOverview();

        return charts;
    }

    public List<Chart> getPredefinedChartEntriesOverview() {
        List<Chart> charts = predefinedChartRepository.getAllChartNames();
        for (Chart chart: charts) {
            Link selfLink = ControllerLinkBuilder.linkTo(ChartsController.class).slash(chart.name.toLowerCase().replace(" ","-")).withSelfRel();
            chart.add(selfLink);
        }
        return charts;
    }

    @RequestMapping(value = "/{chartName}", method = RequestMethod.GET)
    public HttpEntity<ObjectNode> getConfigOfPredefinedChart(@PathVariable("chartName") String chartName) throws IOException {
        String predefinedChartConfig = predefinedChartRepository.getChartByName(chartName);
        ObjectNode node = mapper.readValue(predefinedChartConfig, ObjectNode.class);
        Chart chart = new Chart();
        //chart.config = node.toString();
        return new ResponseEntity<>(node, HttpStatus.OK);
    }
}
