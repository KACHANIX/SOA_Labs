package Entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "applications")
@XmlAccessorType(XmlAccessType.FIELD)
public class Applications {
    @XmlElement(name = "versions__delta")
    public String vers;
    @XmlElement(name = "apps__hashcode")
    public String hash;

    public List<Application> application;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Application {
        public String name;
        public Instance instance;
    }
}
