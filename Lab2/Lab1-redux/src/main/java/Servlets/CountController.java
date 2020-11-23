package Servlets;

import Repository.OrganizationRepository;
import ReturnableEntities.HigherAnnualTurnovers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;

@Path("/organizations/higher-turnovers")
public class CountController {
    private OrganizationRepository repository;

    public CountController() {
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
    public Response getHigherTurnovers(@QueryParam("turnover") double turnover) {
        if (turnover < 1) {
            return Response.status(400).build();
        }
        HigherAnnualTurnovers higher = new HigherAnnualTurnovers(repository.countHigherAnnualTurnover(turnover));
        return Response.ok(higher).build();
    }
}
