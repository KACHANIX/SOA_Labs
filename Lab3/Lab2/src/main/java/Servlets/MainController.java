package Servlets;

import Entities.Applications;
import Entities.Organization;
import Entities.OrganizationModel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

    private int chosen = 11;
    private String app11Address;
    private String app12Address;
    private String lab1Address;
    private final String app11Name = "CLIENT";
    private final String app12Name = "CLIENT1";
    private final String eurekaAppsAddress = "http://localhost:8080/eureka/eureka/apps";
    private final Gson g = new Gson();

    @Path("/fire/all/{id}")
    @POST
    public Response fireAll(@PathParam("id") int id) throws IOException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);
        try {
            checkLab1Running();
        } catch (Throwable ignored) {
            ignored.printStackTrace();
            return Response.status(400).entity(ignored.getMessage()).build();

        }
        HttpsURLConnection con = (HttpsURLConnection) new URL(lab1Address + "/" + id).openConnection();
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

        con = (HttpsURLConnection) new URL(lab1Address + "/" + id).openConnection();
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
    @POST
    public Response acquise(@PathParam("acquirer-id") int acquirerId, @PathParam("acquired-id") int acquiredId) throws IOException, InterruptedException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(localhostAcceptedHostnameVerifier);

        if (acquiredId == acquirerId) {
            return Response.status(400).build();
        }

        // check acquirer to exist
        HttpsURLConnection con = (HttpsURLConnection) new URL(lab1Address + "/" + acquirerId).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String acquirerStr = buffer.lines().collect(Collectors.joining());

        // check acquired to exist
        con = (HttpsURLConnection) new URL(lab1Address + "/" + acquiredId).openConnection();
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
        con = (HttpsURLConnection) new URL(lab1Address + "/" + acquirerId).openConnection();
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
        con = (HttpsURLConnection) new URL(lab1Address + "/" + acquiredId).openConnection();
        con.setRequestMethod("DELETE");
        con.connect();
        responseCode = con.getResponseCode();

        if (responseCode != 200) {
            return Response.status(responseCode).build();
        }
        return Response.ok().build();
    }


    private void checkLab1Running() throws Throwable {
        if (lab1Address != null) {
            // TODO: check if instance is up
            HttpsURLConnection con = (HttpsURLConnection) new URL(lab1Address).openConnection();
            con.setRequestMethod("GET");
            con.connect();
            String responseMessage = con.getResponseMessage();
            if (responseMessage.contains("<html><head><title>Error</title></head><body>404 - Not Found</body></html>")) {
                if (chosen == 11) {
                    chosen = 12;
                    lab1Address = app12Address;
                } else if (chosen == 12) {
                    chosen = 11;
                    lab1Address = app11Address;
                }
            }
        } else {
            HttpURLConnection con = (HttpURLConnection) new  URL(null,eurekaAppsAddress).openConnection();
//            HttpsURLConnection con = (HttpsURLConnection) new URL(eurekaAppsAddress).openConnection();
//            URL url = new URL(eurekaAppsAddress);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            String responseMessage =  (new BufferedReader(new InputStreamReader(con.getInputStream()))).lines().collect(Collectors.joining());

            XmlMapper xmlMapper = new XmlMapper();
            Applications applications = xmlMapper.readValue(responseMessage, Applications.class);
            int apsCount = applications.applications.size();

            if (apsCount > 2 || apsCount == 0) {
                throw new Throwable("There is incorrect number of apps");
            }
            if (apsCount == 1) {
                switch (applications.applications.get(0).name) {
                    case app11Name:
                        app11Address = applications.applications.get(0).instance.homePageUrl;
                        lab1Address = app11Address;
                        chosen = 11;
                        break;
                    case app12Name:
                        app12Address = applications.applications.get(0).instance.homePageUrl;
                        lab1Address = app12Address;
                        chosen = 12;
                        break;
                    default:
                        throw new Throwable("There is a stranger app");
                }
            } else {
                switch (applications.applications.get(0).name) {
                    case app11Name:
                        app11Address = applications.applications.get(0).instance.homePageUrl;
                        break;
                    case app12Name:
                        app12Address = applications.applications.get(0).instance.homePageUrl;
                        break;
                    default:
                        throw new Throwable("There is a stranger app");
                }
                switch (applications.applications.get(1).name) {
                    case app11Name:
                        app11Address = applications.applications.get(1).instance.homePageUrl;
                        break;
                    case app12Name:
                        app12Address = applications.applications.get(1).instance.homePageUrl;
                        break;
                    default:
                        throw new Throwable("There is a stranger app");
                }
                if (chosen == 11) {
                    lab1Address = app11Address;
                } else {
                    lab1Address = app12Address;
                }
            }
        }
    }
}