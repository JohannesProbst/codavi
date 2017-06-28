package at.ac.fhsalzburg.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chart extends ResourceSupport {
    public String name;
    public String config;
}