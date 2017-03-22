package at.ac.fhsalzburg.service.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class DataObjectSchema {


    //Root element of our dataPoint (e.g. array)
    @XmlElement(name="container")
    private String container;

    @XmlElement(name="fields", type=DataProperty.class)
    private List<DataProperty> dataProperties = new ArrayList<>();

    public List<DataProperty> getDataProperties() {
        return dataProperties;
    }

    public DataObjectSchema setDataProperties(List<DataProperty> dataProperties) {
        this.dataProperties = dataProperties;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataObjectSchema)) return false;

        DataObjectSchema that = (DataObjectSchema) o;

        return dataProperties != null ? dataProperties.equals(that.dataProperties) : that.dataProperties == null;

    }

    @Override
    public int hashCode() {
        return dataProperties != null ? dataProperties.hashCode() : 0;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }
}