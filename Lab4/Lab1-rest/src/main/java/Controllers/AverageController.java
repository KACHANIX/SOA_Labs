package Controllers;


import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static Utils.Utils.getMuleURL;


@Path("/organizations/average-turnover")
public class AverageController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAverage() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "average-turnover").openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }
        String responseMessage = IOUtils.toString(con.getInputStream());
        return Response.ok(responseMessage).build();
    }
}
