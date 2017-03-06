package at.ac.fhsalzburg.service.schema;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Johan on 06.03.2017.
 */
@XmlRootElement
public class VegaSchema {

    @XmlElement(name="dataSchema")
    private DataSchema schema;

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
        this.schema = schema;
    }
}
