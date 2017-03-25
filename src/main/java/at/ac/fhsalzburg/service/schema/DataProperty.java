package at.ac.fhsalzburg.service.schema;

public class DataProperty {

    private String field;
    private String selector;
    private String type;
    //private TransformType transformType;

    public String getField() {
        return field;
    }

    public DataProperty setField(String field) {
        this.field = field;
        return this;
    }

    public String getSelector() {
        return selector;
    }

    public DataProperty setSelector(String selector) {
        this.selector = selector;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public DataProperty setType(String type) {
        this.type = type;
        return this;
    }

    /*public TransformType getTransformType() {
        return transformType;
    }

    public DataProperty setTransformType(TransformType transformType) {
        this.transformType = transformType;
        return this;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProperty)) return false;

        DataProperty dataProperty = (DataProperty) o;

        if (field != null ? !field.equals(dataProperty.field) : dataProperty.field != null) return false;
        return selector != null ? selector.equals(dataProperty.selector) : dataProperty.selector == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (selector != null ? selector.hashCode() : 0);
        return result;
    }
}
