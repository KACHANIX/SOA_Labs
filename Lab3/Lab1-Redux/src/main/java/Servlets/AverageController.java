package Servlets;

import Repository.OrganizationRepository;
import ReturnableEntities.AverageAnnualTurnover;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;
@Path("/organizations/average-turnover")
public class AverageController {
    private OrganizationRepository repository;

    public AverageController() {
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
    public Response getAverage() {
        AverageAnnualTurnover avg = new AverageAnnualTurnover(repository.getAverageAnnualTurnover());
        return Response.ok(avg).build();
    }

}
