package Servlets;

import Entities.Organization;
import Entities.OrganizationModel;
import Entities.OrganizationType;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/orgmanager/")
public class MainController {

    private HttpClient client = HttpClient.newHttpClient();
    private String baseAddress = "http://localhost:8080/unnamed/organizations";
    private Gson g = new Gson();

    @Path("/fire/all/{id}")
    @GET
    public Response fireAll(@PathParam("id") int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + id))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return Response.status(404).build();
        }
        String asd = response.body();
        Organization org = g.fromJson(asd, Organization.class);
        org.setEmployees((long) 0);
        OrganizationModel orgModel = new OrganizationModel(org.getName(),
                org.getAnnualTurnover(), org.getCoordinates().getY(),
                org.getCoordinates().getX(), org.getPostalAddress().getStreet(),
                org.getType() == null ? null : (short) org.getType().ordinal(),
                org.getEmployees());
        request = HttpRequest.newBuilder()
                .uri(URI.create(baseAddress + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(g.toJson(orgModel)))
                .build();
        response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return Response.status(response.statusCode()).build();
    }


    @Path("/acquise/{acquirer-id}/{acquired-id}")
    @GET
    public Response acquise(@PathParam("acquirer-id") int acquirerId, @PathParam("acquired-id") int acquiredId) throws IOException, InterruptedException {
        if (acquiredId  == acquirerId){
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