package Servlets;

import Entities.Organization;
import Entities.OrganizationModel;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPatch;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.print.DocFlavor;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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


    private HttpClient client = HttpClient.newHttpClient();
    private String baseAddress = "https://localhost:8443/unnamed/organizations";
    private Gson g = new Gson();

    @Path("/fire/all/{id}")
    @GET
    public Response fireAll(@PathParam("id") int id) throws IOException, InterruptedException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);

        HttpsURLConnection con = (HttpsURLConnection) new URL(baseAddress + "/" + id).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();


//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(baseAddress + "/" + id))
//                .build();
//        HttpResponse<String> response = client.send(request,
//                HttpResponse.BodyHandlers.ofString());
        if (responseCode == 404) {
            return Response.status(404).build();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String asd = buffer.lines().collect(Collectors.joining());


        Organization org = g.fromJson(asd, Organization.class);
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
        os.close();  //don't forget to close the OutputStream
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
        System.out.println(acquirerId);
        System.out.println(acquiredId);
        // check acquirer to exist

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + acquirerId))
                .build();

        HttpResponse<String> acquirerResponse = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(acquirerResponse.statusCode());

        if (acquirerResponse.statusCode() != 200) {
            return Response.status(acquirerResponse.statusCode()).build();
        }

        // check acquired to exist
        request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + acquiredId))
                .build();
        HttpResponse<String> acquiredResponse = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(acquiredResponse.statusCode());

        if (acquiredResponse.statusCode() != 200) {
            return Response.status(acquiredResponse.statusCode()).build();
        }

        // change acquirer's annualTurnover and employees
        Organization acquirer = g.fromJson(acquirerResponse.body(), Organization.class);
        Organization acquired = g.fromJson(acquiredResponse.body(), Organization.class);
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
        request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + acquirerId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(g.toJson(orgModel)))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return Response.status(response.statusCode()).build();
        }

        // delete acquired organization
        request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + acquiredId))
                .DELETE()
                .build();
        response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return Response.status(response.statusCode()).build();
        }
        return Response.ok().build();
    }
}