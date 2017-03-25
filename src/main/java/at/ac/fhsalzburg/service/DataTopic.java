package at.ac.fhsalzburg.service;

import at.ac.fhsalzburg.service.schema.DataProperty;
import at.ac.fhsalzburg.service.schema.DataObjectSchema;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import at.ac.fhsalzburg.service.schema.TransformType;
import com.google.common.base.Strings;
import net.fortuna.ical4j.model.DateTime;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Johan on 05.03.2017.
 */
public class DataTopic extends Topic {

    private DataObjectSchema schema;
    private String datasource;

    public DataTopic(String name, MessageSendingOperations<String> messagingTemplate, String datasource, DataObjectSchema schema) {
        super(name, messagingTemplate);
        this.datasource = datasource;
        this.schema = schema;
    }

    @Override
    public void run() {
        // Next steps: Daten von datasource laden
        //              Dynamisches Schema aus moxy verwenden um daten http://www.eclipse.org/eclipselink/documentation/2.5/solutions/jpatoxml006.htm
        //              Daten welche im Dataschema angegeben sind extrahieren und zurückgeben.

        setRunning(true);
        try {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, createDynamicBindings(this.schema));

        final DynamicJAXBContext context = DynamicJAXBContextFactory.createContextFromOXM(null, properties);
        final Unmarshaller um = context.createUnmarshaller();
        final String inputClass = Strings.isNullOrEmpty(this.schema.getContainer()) ? "dynamic.inputDataObject" :  "dynamic.inputDataContainer" ;
        final Class<? extends DynamicEntity> resultsClass = context.newDynamicEntity(inputClass).getClass();

        final Marshaller m = context.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);


        while(true) {
            // Hier daten Daten laden und verarbeiten....
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(datasource, String.class);

            if (response.getHeaders().getContentType().toString().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                um.setProperty("eclipselink.media-type", "application/json");
                um.setProperty("eclipselink.json.include-root", false);
                um.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            }


            InputStream inputStream = new ByteArrayInputStream(response.getBody().getBytes(StandardCharsets.UTF_8));
            DynamicEntity in = (DynamicEntity) um.unmarshal(new StreamSource(inputStream), resultsClass).getValue();

            //TODO: find test-data with data for bar-chart, pi-chart and line-chart tests
            //TODO: introduce multiple java-script handler for different data handling

            ArrayList<DynamicEntity> content = new ArrayList<DynamicEntity>();
            if(Strings.isNullOrEmpty(this.schema.getContainer())){
                DynamicEntity out = context.newDynamicEntity("dynamic.outputDataObject");
                for (DataProperty property : schema.getDataProperties()) {
                    //TODO: if transform is set do transformation here
                    //Optional<TransformType> transformType = Optional.of(property.getTransformType());
                    //transformType.ifPresent(tt -> tt.convert(property.getField()));
                    /*if(property.getTransformType() != null){
                        DateTime dateTime = (DateTime) property.getTransformType().convert(in.get(property.getField()));
                        out.set(property.getField(), dateTime);
                    }*/
                    out.set(property.getField(), in.get(property.getField()));
                }
                content.add(out);
            } else {
                for (DynamicEntity dataPoint : (List<DynamicEntity>) in.get("dataPointList")) {
                    DynamicEntity out = context.newDynamicEntity("dynamic.outputDataObject");
                    for (DataProperty property : schema.getDataProperties()) {
                        out.set(property.getField(), dataPoint.get(property.getField()));
                    }
                    content.add(out);
                }
            }
            DynamicEntity list = context.newDynamicEntity("dynamic.outputData");
            list.set("dataPointList", content);
            OutputStream outputStream = new ByteArrayOutputStream();
            m.marshal(list, outputStream);

            send(outputStream.toString());

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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String createDynamicBindings(DataObjectSchema schema) {

        StringBuilder builder = new StringBuilder(4096);
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\" package-name=\"dynamic\">");
        // Input data from Datasource
        builder.append("<java-types>");

        builder.append("<java-type name=\"dynamic.inputDataContainer\"> <xml-root-element/> <java-attributes>" )
            .append("<xml-element java-attribute=\"dataPointList\" xml-path=\"")
                .append(schema.getContainer()).append("\"")
                .append(" type=\"dynamic.inputDataObject\"")
                .append(" container-type=\"java.util.ArrayList\"")
                .append("/>")
                .append("</java-attributes> </java-type>");

        builder.append("<java-type name=\"dynamic.inputDataObject\"> <xml-root-element/> <java-attributes>");
        for(DataProperty property : schema.getDataProperties()){
            builder.append("<xml-element java-attribute=\"")
                    .append(property.getField())
                    .append("\" xml-path=\"")
                    .append(property.getSelector())
                    .append("\" type=\"")
                    .append(property.getType())
                    .append("\"/>");
        }
        builder.append("</java-attributes> </java-type>");
        // Output data for Vega

        // Container with list of dynamic.out
        builder.append("<java-types>");
        builder.append("<java-type name=\"dynamic.outputData\"> <xml-root-element/> <java-attributes>" )
                .append("<xml-element java-attribute=\"dataPointList\"")
                .append(schema.getContainer()).append("\"")
                .append(" type=\"dynamic.outputDataObject\"")
                .append(" container-type=\"java.util.ArrayList\"")
                .append("/>")
                .append("</java-attributes> </java-type>");

        builder.append("<java-type name=\"dynamic.outputDataObject\"> <java-attributes>");
        for(DataProperty point : schema.getDataProperties()) {
                    builder.append("<xml-element java-attribute=\"")
                            .append(point.getField())
                            .append("\" xml-path=\"")
                            .append(point.getField())
                            .append("/text()\" type=\"")
                            .append(point.getType())
                            .append("\"/>");
        }
        builder.append("</java-attributes> </java-type>");

                builder.append("</java-types></xml-bindings>");
        return new String(builder);
    }
}
