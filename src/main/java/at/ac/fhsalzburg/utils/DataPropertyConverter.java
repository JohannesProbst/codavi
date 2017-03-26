package at.ac.fhsalzburg.utils;

public interface DataPropertyConverter <T extends Comparable> {

    T convert(String value);
}
