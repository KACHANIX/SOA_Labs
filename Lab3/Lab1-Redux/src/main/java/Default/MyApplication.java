package Default;

import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//@Component
@ApplicationPath("/*")
public class MyApplication extends Application {
    public MyApplication(){

    }
}
