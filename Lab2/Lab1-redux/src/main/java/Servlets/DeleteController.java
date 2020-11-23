package Servlets;

import Repository.OrganizationRepository;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;

@Path("/organizations/equal-turnover")
public class DeleteController {

    private OrganizationRepository repository;

    public DeleteController() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    @DELETE
    public Response deleteEqualTurnover(@QueryParam("turnover") Double turnover) {
        if (turnover < 1) {
            return Response.status(400).build();
        }
        repository.deleteOrganizationsWithEqualTurnover(turnover);
        return Response.ok().build();
    }
}
