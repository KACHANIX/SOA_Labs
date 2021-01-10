package Controllers;

import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static Utils.Utils.*;

@Path("/organizations/higher-turnovers")
public class CountController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHigherTurnovers(@QueryParam("turnover") String turnover) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "higher-turnovers/" + turnover).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        //  404 is when no endpoint found in mule (meaning bad url)
        if (responseCode == 404) {
            return Response.status(400).build();
        } else if (responseCode == 500) {
            String responseMessage = IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.ok(IOUtils.toString(con.getInputStream())).build();
    }
}
