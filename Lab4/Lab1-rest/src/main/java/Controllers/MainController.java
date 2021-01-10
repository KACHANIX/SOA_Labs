package Controllers;

import Entities.OrganizationModel;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static Utils.Utils.*;

@Path("/organizations")
public class MainController {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("sortBy") String sortBy,
                        @QueryParam("filterBy") String filterBy,
                        @QueryParam("page") String page) throws Exception {
        String url = getMuleURL() + "organizations";
        int count = 0;
        if (sortBy != null) {
            url += "?sortBy=" + sortBy;
            count++;
        }
        if (filterBy != null) {
            if (count != 0) {
                url += "&";
            } else {
                url += "?";
            }
            url += "filterBy=" + filterBy;
            count++;
        }
        if (page != null) {
            if (count != 0) {
                url += "&";
            } else {
                url += "?";
            }
            url += "page=" + page;
        }
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        String responseMessage;
        //  404 is when no endpoint found in mule (meaning bad url)
        if (responseCode == 404) {
            return Response.status(400).build();
        } else if (responseCode == 500) {
            responseMessage = IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            throw new Exception(responseMessage);

        }
        responseMessage = IOUtils.toString(con.getInputStream());

        if (responseMessage == null || responseMessage.equals("null")) {
            return Response.ok("[]").build();
        }
        return Response.ok(responseMessage).build();
    }

    @POST
    public Response addOrganization(OrganizationModel organizationModel) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleURL() + "add").openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
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
            String responseMessage = IOUtils.toString(con.getInputStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.ok().build();

    }
}