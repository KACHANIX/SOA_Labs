package Servlets;

import Model.Organization;
import Repository.OrganizationRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import static Utils.Utils.*;

@WebServlet("/organizations/*")
public class IdServlet extends HttpServlet {
    private OrganizationRepository repository;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
        }
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // just get particular
        String idStr;
        try {
            idStr = URLDecoder.decode(req.getRequestURL().toString().split("organizations/")[1], "UTF-8");

        } catch (Exception e) // if empty string
        {
            resp.setStatus(422);
            return;
        }

        int id;
        if (tryParseInt(idStr)) {
            id = Integer.parseInt(idStr);
        } else {
            resp.setStatus(422);
            return;
        }
        if (id < 1) {
            resp.setStatus(422);
            return;
        }

        Organization organization = repository.getById(id);
        if (organization != null) {
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(gson.toJson(organization));
            return;
        } else {
            resp.setStatus(404);
            return;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String idStr;
        try {
            idStr = URLDecoder.decode(req.getRequestURL().toString().split("organizations/")[1], "UTF-8");
        } catch (Exception e) // if empty string
        {
            resp.setStatus(422);
            return;
        }

        if (!tryParseInt(idStr)) {
            resp.setStatus(422);
            return;
        }
        int id = Integer.parseInt(idStr);

        Organization org = repository.getById(id);
        if (org == null) {
            resp.setStatus(404);
            return;
        }

        if (id < 1) {
            resp.setStatus(422);
            return;
        }
        repository.deleteOrganization(id);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr;
        try {
            idStr = URLDecoder.decode(req.getRequestURL().toString().split("organizations/")[1], "UTF-8");
        } catch (Exception e) // if empty string
        {
            resp.setStatus(422);
            return;
        }

        int id;
        if (tryParseInt(idStr)) {
            id = Integer.parseInt(idStr);
        } else {
            resp.setStatus(422);
            return;
        }
        if (id < 1) {
            resp.setStatus(422);
            return;
        }
        Organization org = repository.getById(id);
        if (org == null) {
            resp.setStatus(404);
            return;
        }

        String queryString = req.getQueryString();
        StringBuffer jb = new StringBuffer();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null)
            jb.append(line);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jb.toString());
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }
        String name;
        Long y;
        Double annualTurnover;
        try {
            name = jsonObject.getString("name");
            y = jsonObject.getLong("y");
            annualTurnover = jsonObject.getDouble("turnover");
        } catch (Exception e) {
            resp.setStatus(422);
            return;
        }
        if (annualTurnover <= 0 || name.isEmpty()) {
            resp.setStatus(422);
            return;
        }
        Double x = null;
        String street = null;
        Short type = null;
        try {
            x = jsonObject.getDouble("x");
        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            if (exceptionMessage.contains("is not a double")) {
                resp.setStatus(422);
                return;
            }
        }
        try {
            street = jsonObject.getString("street");
        } catch (Exception e) {
        }

        try {
            type = Short.parseShort(String.valueOf(jsonObject.getInt("type")));
        } catch (JSONException e) {
            String exceptionMessage = e.getMessage();
            if (exceptionMessage.contains("is not a int")) {
                resp.setStatus(422);
                return;
            }
        }

        if (type != null && (type > 4 || type < 0)) {
            resp.setStatus(422);
            return;
        }

        repository.updateOrganization(id, name, x, y, annualTurnover, street, type);
    }
}
