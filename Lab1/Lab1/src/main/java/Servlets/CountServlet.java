package Servlets;

import Repository.OrganizationRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import static Utils.Utils.*;

@WebServlet("/organizations/higher-turnovers")
public class CountServlet extends HttpServlet {
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
        String queryString = req.getQueryString();
        if (queryString == null) {
            resp.setStatus(422);
            return;
        }

        Map<String, String> paramsMap;
        try {
            paramsMap = splitQuery(queryString);
        } catch (Exception e) {
            resp.setStatus(400);
            return;
        }
        if (!tryParseDouble(paramsMap.get("turnover"))) {
            resp.setStatus(422);
            return;
        }

        double annualTurnover = Double.parseDouble(paramsMap.get("turnover"));

        if (annualTurnover <= 0) {
            resp.setStatus(422);
            return;
        }

        long count = repository.countHigherAnnualTurnover(annualTurnover);

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write("{\"Count\":");
        writer.write(String.valueOf(count));
        writer.write("}");
    }
}
