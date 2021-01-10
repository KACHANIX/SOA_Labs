package Controllers;

import org.apache.commons.io.IOUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static Utils.Utils.getMuleURL;

@Path("/organizations/equal-turnover")
public class DeleteController {


    @DELETE
    public Response deleteEqualTurnover(@QueryParam("turnover") String turnover) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "annual-turnover/" + turnover).openConnection();
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
        return Response.ok().build();
    }
}
