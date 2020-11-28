package Servlets;

import Entities.Organization;
import Entities.OrganizationModel;
import Repository.OrganizationRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;

@Path("/organizations/{id}")
public class IdController {
    private OrganizationRepository repository;

    public IdController() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        if (id < 1) {
            return Response.status(400).build();
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            return Response.ok(organization).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    public Response deleteById(@PathParam("id") int id) {
        if (id < 1) {
            return Response.status(400).build();
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            repository.deleteOrganization(id);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrganization(@PathParam("id") int id, OrganizationModel organizationModel) {
        if (id < 1) {
            return Response.status(400).build();
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null ||
                    organizationModel.turnover < 1 || organizationModel.name.isEmpty() || organizationModel.x == null||
                    organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0) ||
                    organizationModel.employees != null && organizationModel.employees < 0) {
                return Response.status(400).build();
            }
            repository.updateOrganization(id, organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.street, organizationModel.type, organizationModel.employees);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
