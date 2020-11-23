package Servlets;

import Model.Organization;
import Repository.OrganizationRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import static Utils.Utils.*;

@WebServlet("/organizations/average-turnover")
public class AverageServlet extends HttpServlet {
    private OrganizationRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        double avg  = repository.getAverageAnnualTurnover();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write("{\"AverageTurnover\":");
        writer.write(String.valueOf(avg));
        writer.write("}");
    }
}
