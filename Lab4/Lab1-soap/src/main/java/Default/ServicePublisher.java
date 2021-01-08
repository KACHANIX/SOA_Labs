package Default;

import javax.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/1/Service", new ServiceImpl());
        System.out.println("DAFUQ");
    }
}
