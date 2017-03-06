package at.ac.fhsalzburg.service.schema;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataSchema {

    @XmlElement(name="fields")
    private List<DataPoint> dataPoints;

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public DataSchema setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSchema)) return false;

        DataSchema that = (DataSchema) o;

        return dataPoints != null ? dataPoints.equals(that.dataPoints) : that.dataPoints == null;

    }

    @Override
    public int hashCode() {
        return dataPoints != null ? dataPoints.hashCode() : 0;
    }
}