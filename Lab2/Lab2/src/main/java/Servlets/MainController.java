package Servlets;

import Entities.Organization;
import Entities.OrganizationModel;
import com.google.gson.Gson;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

@Path("/orgmanager/")
public class MainController {
    final HostnameVerifier defaultHostnameVerifier = javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier();
    final HostnameVerifier localhostAcceptedHostnameVerifier = (hostname, sslSession) -> {
        if (hostname.equals("localhost")) {
            return true;
        }
        return defaultHostnameVerifier.verify(hostname, sslSession);
    };

    private final String baseAddress = "https://localhost:8443/unnamed/organizations";
    private final Gson g = new Gson();

    @Path("/fire/all/{id}")
    @GET
    public Response fireAll(@PathParam("id") int id) throws IOException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);

        HttpsURLConnection con = (HttpsURLConnection) new URL(baseAddress + "/" + id).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode == 404) {
            return Response.status(404).build();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));

        Organization org = g.fromJson(buffer.lines().collect(Collectors.joining()), Organization.class);
        org.setEmployees((long) 0);
        OrganizationModel orgModel = new OrganizationModel(org.getName(),
                org.getAnnualTurnover(), org.getCoordinates().getY(),
                org.getCoordinates().getX(), org.getPostalAddress().getStreet(),
                org.getType() == null ? null : (short) org.getType().ordinal(),
                org.getEmployees());

        con = (HttpsURLConnection) new URL(baseAddress + "/" + id).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(g.toJson(orgModel));
        osw.flush();
        osw.close();
        os.close();
        con.connect();

        return Response.status(con.getResponseCode()).build();
    }


    @Path("/acquise/{acquirer-id}/{acquired-id}")
    @GET
    public Response acquise(@PathParam("acquirer-id") int acquirerId, @PathParam("acquired-id") int acquiredId) throws IOException, InterruptedException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);

        if (acquiredId == acquirerId) {
            return Response.status(400).build();
        }

        // check acquirer to exist
        HttpsURLConnection con = (HttpsURLConnection) new URL(baseAddress + "/" + acquirerId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String acquirerStr = buffer.lines().collect(Collectors.joining());

        // check acquired to exist
        con = (HttpsURLConnection) new URL(baseAddress + "/" + acquiredId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        responseCode = con.getResponseCode();

        if (responseCode != 200) {
            return Response.status(responseCode).build();
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
        con = (HttpsURLConnection) new URL(baseAddress + "/" + acquirerId).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(g.toJson(orgModel));
        osw.flush();
        osw.close();
        os.close();  //don't forget to close the OutputStream
        con.connect();
        responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }

        // delete acquired organization
        con = (HttpsURLConnection) new URL(baseAddress + "/" + acquiredId).openConnection();
        con.setRequestMethod("DELETE");
        con.connect();
        responseCode = con.getResponseCode();

        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }
        return Response.ok().build();
    }
}