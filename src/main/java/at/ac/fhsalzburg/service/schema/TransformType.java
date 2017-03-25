package at.ac.fhsalzburg.service.schema;

import at.ac.fhsalzburg.utils.DataPropertyConverter;
import at.ac.fhsalzburg.utils.TimeValueConverter;

public enum TransformType implements DataPropertyConverter {

    TIMESTAMP(TimeValueConverter::toTimestamp);

    private DataPropertyConverter converter;

    TransformType(DataPropertyConverter converter) {
        this.converter = converter;
    }

    @Override
    public Comparable convert(String value) {
        return converter.convert(value);
    }



}
