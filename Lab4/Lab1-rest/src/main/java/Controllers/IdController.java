package Controllers;

import Entities.OrganizationModel;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static Utils.Utils.getMuleURL;

@Path("/organizations/{id}")
public class IdController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "id/" + id).openConnection();
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
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.ok(IOUtils.toString(con.getInputStream())).build();
    }

    @DELETE
    public Response deleteById(@PathParam("id") String id) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "delete-id/" + id).openConnection();
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
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrganization(@PathParam("id") String id, OrganizationModel organizationModel) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "update/" + id).openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        Gson g = new Gson();
        osw.write(g.toJson(organizationModel));
        osw.flush();
        osw.close();
        os.close();
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
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.ok().build();

    }
}
