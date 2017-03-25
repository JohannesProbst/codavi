package at.ac.fhsalzburg.utils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import net.fortuna.ical4j.model.DateTime;
import java.util.List;

public class TimeValueConverter {

    //public TimeValueConverter(){}

    public static DateTime toTimestamp(String value){
        Parser parser = new Parser();
        List<DateGroup> r = parser.parse(value);
        if (r.isEmpty() || r.get(0).getDates().isEmpty()) {
            return null;
        }
        //TODO: set TimeZone on CET
        return new DateTime(r.get(0).getDates().get(0));
    }
}
