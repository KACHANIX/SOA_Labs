package Servlets;

import Entities.Organization;
import Entities.OrganizationModel;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

import static Utils.Utils.getMuleUrl;

@Path("/orgmanager/")
public class MainController {
//    final HostnameVerifier defaultHostnameVerifier = javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier();
//    final HostnameVerifier localhostAcceptedHostnameVerifier = (hostname, sslSession) -> {
//        if (hostname.equals("localhost")) {
//            return true;
//        }
//        return defaultHostnameVerifier.verify(hostname, sslSession);
//    };

    private final Gson g = new Gson();

    @Path("/fire/all/{id}")
    @POST
    public Response fireAll(@PathParam("id") String id) throws Exception {
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);

        HttpURLConnection con = (HttpURLConnection) new URL(getMuleUrl() + "id/" + id).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        String responseMessage;
        if (responseCode == 404) {
            return Response.status(400).build();
        } else if (responseCode == 500) {
            responseMessage= IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));

        Organization org = g.fromJson(buffer.lines().collect(Collectors.joining()), Organization.class);
        org.setEmployees((long) 0);
        OrganizationModel orgModel = new OrganizationModel(org.getName(),
                org.getAnnualTurnover(), org.getCoordinates().getY(),
                org.getCoordinates().getX(), org.getPostalAddress().getStreet(),
                org.getType() == null ? null : (short) org.getType().ordinal(),
                org.getEmployees());

        con = (HttpURLConnection) new URL(getMuleUrl() + "update/" + id).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(g.toJson(orgModel));
        osw.flush();
        osw.close();
        os.close();
        con.connect();

        responseCode = con.getResponseCode();
        if (responseCode == 500) {
            responseMessage = IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        return Response.status(con.getResponseCode()).build();
    }


    @Path("/acquise/{acquirer-id}/{acquired-id}")
    @POST
    public Response acquise(@PathParam("acquirer-id") String acquirerId, @PathParam("acquired-id") String acquiredId) throws Exception {

        if (acquiredId == acquirerId) {
            return Response.status(400).build();
        }

        // check acquirer to exist
        HttpURLConnection con = (HttpURLConnection) new URL(getMuleUrl() + "id/" + acquirerId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode == 404) {
            return Response.status(400).build();
        } else if (responseCode == 500) {
            String responseMessage= IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String acquirerStr = buffer.lines().collect(Collectors.joining());

        // check acquired to exist
        con = (HttpURLConnection) new URL(getMuleUrl() + "id/" + acquiredId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        responseCode = con.getResponseCode();
        if (responseCode == 404) {
            return Response.status(400).build();
        } else if (responseCode == 500) {
            String responseMessage= IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
        buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String acquiredStr = buffer.lines().collect(Collectors.joining());

        // change acquirer's annualTurnover and employees
        Organization acquirer = g.fromJson(acquirerStr, Organization.class);
        Organization acquired = g.fromJson(acquiredStr, Organization.class);
        Long employees = null;
        if (acquirer.getEmployees() != null && acquired.getEmployees() != null) {
            employees = acquirer.getEmployees() + acquired.getEmployees();
        } else if (acquirer.getEmployees() != null && acquired.getEmployees() == null) {
            employees = acquirer.getEmployees();
        } else if (acquirer.getEmployees() == null && acquired.getEmployees() != null) {
            employees = acquired.getEmployees();
        }
        OrganizationModel orgModel = new OrganizationModel(acquirer.getName(),
                acquirer.getAnnualTurnover() + acquired.getAnnualTurnover(),
                acquirer.getCoordinates().getY(),
                acquirer.getCoordinates().getX(),
                acquirer.getPostalAddress().getStreet(),
                acquirer.getType() == null ? null : (short) acquirer.getType().ordinal(),
                employees);
        con = (HttpURLConnection) new URL(getMuleUrl() + "update/" + acquirerId).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(g.toJson(orgModel));
        osw.flush();
        osw.close();
        os.close();  //don't forget to close the OutputStream
        con.connect();
        responseCode = con.getResponseCode();

        if (responseCode == 500) {
            String responseMessage= IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
//        if (responseCode != 200) {
//            return Response.status(responseCode).build();
//        }

        // delete acquired organization
        con = (HttpURLConnection) new URL(getMuleUrl() + "delete-id/" + acquiredId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        responseCode = con.getResponseCode();
        if (responseCode == 500) {
            String responseMessage= IOUtils.toString(con.getErrorStream());
            if (responseMessage.contains("Unmarshalling Error:") || responseMessage.contains("Bad request")) {
                return Response.status(400).build();
            }
            if (responseMessage.contains("Not found")) {
                return Response.status(404).build();
            }
            throw new Exception(responseMessage);
        }
//        if (responseCode != 200) {
//            return Response.status(responseCode).build();
//        }
        return Response.ok().build();
    }
}