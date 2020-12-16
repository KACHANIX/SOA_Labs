package Default;

import Servlets.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig()
    {
        register(CorsFilter.class);
//        register(MyApplication.class);
        register(MainController.class);
        register(IdController.class);
        register(DeleteController.class);
        register(CountController.class);
        register(AverageController.class);
    }
}
