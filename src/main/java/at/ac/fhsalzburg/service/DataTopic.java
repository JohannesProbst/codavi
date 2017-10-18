package at.ac.fhsalzburg.service;

import at.ac.fhsalzburg.service.schema.DataProperty;
import at.ac.fhsalzburg.service.schema.DataObjectSchema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.fortuna.ical4j.model.DateTime;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.springframework.http.*;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DataTopic extends Topic {

    private DataObjectSchema schema;
    private String datasource;

    public DataTopic(String name, MessageSendingOperations<String> messagingTemplate, String datasource, DataObjectSchema schema, Long interval) {
        super(name, messagingTemplate, interval);
        this.datasource = datasource;
        this.schema = schema;
    }

    @Override
    public void run() {
        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
        setRunning(true);
        try {
        Map<String, Object> properties = new HashMap<String, Object>();
        InputStream is = new ByteArrayInputStream(createDynamicBindings(this.schema).getBytes(StandardCharsets.UTF_8));
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, is);

        final DynamicJAXBContext context = DynamicJAXBContextFactory.createContextFromOXM(null, properties);
        final Unmarshaller um = context.createUnmarshaller();
        final String inputClass = Strings.isNullOrEmpty(this.schema.getContainer()) ? "dynamic.inputDataObject" :  "dynamic.inputDataContainer" ;
        final Class<? extends DynamicEntity> resultsClass = context.newDynamicEntity(inputClass).getClass();

        final Marshaller m = context.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);


        while(true) {
            // Hier daten Daten laden und verarbeiten....
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(datasource, HttpMethod.GET, entity, String.class);
            InputStream inputStream = new ByteArrayInputStream(response.getBody().getBytes(StandardCharsets.UTF_8));
            if (response.getHeaders().getContentType().toString().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                um.setProperty("eclipselink.media-type", "application/json");
                um.setProperty("eclipselink.json.include-root", false);
                um.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            }
            if (response.getHeaders().getContentType().toString().startsWith(MediaType.TEXT_HTML_VALUE)){
                throw new Exception("Resources with content-type: text/html are not processable so far.");
            }

            Object o =  um.unmarshal(new StreamSource(inputStream), resultsClass).getValue();
            List<DynamicEntity> inputDate = null;
            if(o instanceof List){
                //atm only one datapoint is processed
                inputDate = ((List)o);
            } else {
                inputDate = ((((DynamicEntity)o).get("dataPointList")));
            }

            //atm there are not multiple java-script handlers for different data handling



            List<DynamicEntity> content = new ArrayList<>();
            for(DynamicEntity in : inputDate) {
                if (Strings.isNullOrEmpty(this.schema.getContainer())) {
                    DynamicEntity out = context.newDynamicEntity("dynamic.outputDataObject");
                    for (DataProperty property : schema.getDataProperties()) {
                        Object fieldValue = in.get(property.getField());
                        if (property.getTransformType() != null) {
                            //Instant dateTime = (Instant) property.getTransformType().convert(fieldValue.toString());
                            StringBuffer sb = new StringBuffer("new Date(");
                            //sb.append(dateTime.toEpochMilli());
                            sb.append(String.valueOf(fieldValue));
                            sb.append(").getTime()");
                            out.set(property.getField(), sb.toString());
                        } else {
                            out.set(property.getField(), fieldValue);
                        }
                    }
                    content.add(out);
                } else {

                        DynamicEntity out = context.newDynamicEntity("dynamic.outputDataObject");
                        for (DataProperty property : schema.getDataProperties()) {
                            Object fieldValue = in.get(property.getField());
                            if (property.getTransformType() != null) {
                                //Instant dateTime = (Instant) property.getTransformType().convert(fieldValue.toString());
                                StringBuffer sb = new StringBuffer("new Date(");
                                //sb.append(dateTime.toEpochMilli());
                                sb.append(String.valueOf(fieldValue));
                                sb.append(").getTime()");
                                out.set(property.getField(), sb.toString());
                            } else {
                                out.set(property.getField(), fieldValue);
                            }
                        }
                        content.add(out);

                }
            }
            DynamicEntity list = context.newDynamicEntity("dynamic.outputDataContainer");
            list.set("dataPointList", content);
            OutputStream outputStream = new ByteArrayOutputStream();
            m.marshal(list, outputStream);

            send(outputStream.toString());

            try {
                if(this.interval == 0){
                    while(true){
                        if(this.interval != 0){
                            break;
                        }
                        if(!this.isSubscribed()){
                            setRunning(false);
                            return;
                        }
                    }
                }else {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(this.interval));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(!this.isSubscribed()){
                setRunning(false);
                return;
            }
        }
        }catch (Exception ex){
            send(ex.toString());
            ex.printStackTrace();
        }
    }

    private String createDynamicBindings(DataObjectSchema schema) {

        StringBuilder builder = new StringBuilder(4096);
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\" package-name=\"dynamic\">");
        // Input data from Datasource
        builder.append("<java-types>");

        //if(schema.getContainer() != null) {
            builder.append("<java-type name=\"dynamic.inputDataContainer\"> <xml-root-element/> <java-attributes>")
                    .append("<xml-element java-attribute=\"dataPointList\" xml-path=\"")
                    .append(schema.getContainer()).append("\"")
                    .append(" type=\"dynamic.inputDataObject\"")
                    .append(" container-type=\"java.util.ArrayList\"")
                    .append("/>")
                    .append("</java-attributes> </java-type>");
        //}

        builder.append("<java-type name=\"dynamic.inputDataObject\"> <xml-root-element/> <java-attributes>");
        for(DataProperty property : schema.getDataProperties()){
            builder.append("<xml-element java-attribute=\"")
                    .append(property.getField())
                    .append("\" xml-path=\"")
                    .append(StringEscapeUtils.escapeXml(property.getSelector()))
                    .append("\" type=\"")
                    .append(property.getType())
                    .append("\"/>");
        }
        builder.append("</java-attributes> </java-type>");
        // Output data for Vega

        // Container with list of dynamic.out
        //if(schema.getContainer() != null) {
            //builder.append("<java-types>");
            builder.append("<java-type name=\"dynamic.outputDataContainer\"> <xml-root-element/> <java-attributes>")
                    .append("<xml-element java-attribute=\"dataPointList\" xml-path=\"")
                    .append("table").append("\"")
                    .append(" type=\"dynamic.outputDataObject\"")
                    .append(" container-type=\"java.util.ArrayList\"")
                    .append("/>")
                    .append("</java-attributes> </java-type>");
        //}
        builder.append("<java-type name=\"dynamic.outputDataObject\"> <java-attributes>");
        for(DataProperty point : schema.getDataProperties()) {
                    builder.append("<xml-element java-attribute=\"")
                            .append(point.getField())
                            .append("\" xml-path=\"")
                            .append(StringEscapeUtils.escapeXml(point.getField()))
                            .append("/text()\" type=\"")
                            .append(point.getType())
                            .append("\"/>");
        }
        builder.append("</java-attributes> </java-type>");

                builder.append("</java-types></xml-bindings>");
        return new String(builder);
    }
}
