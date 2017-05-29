package at.ac.fhsalzburg.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/uuids")
public class ActiveChartsController {

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String ActiveChartsMenu(){

        String chartJsonSchema = getActiveChartsEntriesOverview();

        return chartJsonSchema;
    }

    public String getActiveChartsEntriesOverview() {
        return "uuids menu";
    }


    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public HttpEntity<ObjectNode> getActiveChartConfig(@PathVariable("uuid") String chartUuid) throws IOException, JsonMappingException {

        String json = "{ \"width\": 400,   \"height\": 200,   \"padding\": {\"top\": 10, \"left\": 30, \"bottom\": 30, \"right\": 10},   \"data\": [     {       \"name\": \"table\"  }   ],   \"scales\": [     {       \"name\": \"x\",       \"type\": \"ordinal\",       \"range\": \"width\",       \"domain\": {\"data\": \"table\", \"field\": \"x\"}     },     {       \"name\": \"y\",       \"type\": \"linear\",       \"range\": \"height\",       \"domain\": {\"data\": \"table\", \"field\": \"y\"},       \"nice\": true     }   ],   \"axes\": [     {\"type\": \"x\", \"scale\": \"x\"},     {\"type\": \"y\", \"scale\": \"y\"}   ],   \"marks\": [     {       \"type\": \"rect\",       \"from\": {\"data\": \"table\"},       \"properties\": {         \"enter\": {           \"x\": {\"scale\": \"x\", \"field\": \"x\"},           \"width\": {\"scale\": \"x\", \"band\": true, \"offset\": -1},           \"y\": {\"scale\": \"y\", \"field\": \"y\"},           \"y2\": {\"scale\": \"y\", \"value\": 0}         },         \"update\": {           \"fill\": {\"value\": \"steelblue\"}         },         \"hover\": {           \"fill\": {\"value\": \"red\"}         }       }     }   ] }";
        ObjectNode node = mapper.readValue(json, ObjectNode.class);

        return new ResponseEntity<ObjectNode>(node,HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public HttpEntity<String> updateActiveChartConfig(@PathVariable("uuid") String chartUuid, @RequestBody ObjectNode chartConfig){

        Boolean isConfigEmpty = chartConfig.isNull();
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
