package at.ac.fhsalzburg.service.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataPoint {

    private String field;
    private String selector;

    public String getField() {
        return field;
    }

    public DataPoint setField(String field) {
        this.field = field;
        return this;
    }

    public String getSelector() {
        return selector;
    }

    public DataPoint setSelector(String selector) {
        this.selector = selector;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;

        if (field != null ? !field.equals(dataPoint.field) : dataPoint.field != null) return false;
        return selector != null ? selector.equals(dataPoint.selector) : dataPoint.selector == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (selector != null ? selector.hashCode() : 0);
        return result;
    }
}
