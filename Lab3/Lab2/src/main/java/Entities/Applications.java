package Entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Applications {
    public String versions_delta;
    public String apps_hashcode;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Application> applications;

}
