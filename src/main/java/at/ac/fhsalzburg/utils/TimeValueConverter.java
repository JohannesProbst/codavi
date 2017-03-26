package at.ac.fhsalzburg.utils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import net.fortuna.ical4j.model.DateTime;


import java.util.List;
import java.util.TimeZone;

public class TimeValueConverter {

    //public TimeValueConverter(){}

    public static DateTime toTimestamp(String value){
        Parser parser = new Parser(TimeZone.getTimeZone("UTC"));
        List<DateGroup> r = parser.parse(value);
        if (r.isEmpty() || r.get(0).getDates().isEmpty()) {
            return null;
        }
        return new DateTime(r.get(0).getDates().get(0));
    }
}
