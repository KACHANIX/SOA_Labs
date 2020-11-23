package Servlets;

import Model.Organization;
import Model.Page;
import Repository.OrganizationRepository;
import com.google.gson.Gson;
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
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static Utils.Utils.*;

@WebServlet("/organizations")
public class MainServlet extends HttpServlet {

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
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryString = req.getQueryString();
        ArrayList<Organization> organizations;
        if (queryString == null) {
            organizations = repository.getAllOrganizations();
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(gson.toJson(organizations));
            return;
        }
        Map<String, String> paramsMap;
        try {
            paramsMap = splitQueryWithoutDecoding(queryString);
        } catch (Exception e) {
            resp.setStatus(400);
            return;
        }

        String[] tmp = null;
        Map<String, String> filters = null;
        Map<String, String> sorts = null;
        Page pagination = null;

        long mustBe = 0;
        long count = 0;
        if (paramsMap.get("filterBy") != null) {
            filters = new LinkedHashMap<>();
            String tmpString = paramsMap.get("filterBy");
            try {
                tmp = tmpString.split(",");
            } catch (Exception e) {
                resp.setStatus(400);
                return;
            }

            String filterValue;
            String filterName;
            mustBe = tmpString.chars().filter(ch -> ch == ',').count();

            for (String filterStr : tmp) {
                try {
                    filterName = URLDecoder.decode(filterStr.substring(0, filterStr.indexOf('!')), "UTF-8");
                    filterValue = URLDecoder.decode(filterStr.substring(filterStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    resp.setStatus(400);
                    return;
                }

                if (!fields.contains(filterName)) {
                    resp.setStatus(400);
                    return;
                }

                switch (filterName) {
                    case "id":
                        if (!tryParseInt(filterValue)) {
                            resp.setStatus(422);
                            return;
                        } else if (Integer.parseInt(filterValue) <= 0) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                    case "name":
                    case "date":
                        if (filterValue.isEmpty()) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                    case "turnover":
                        if (!tryParseDouble(filterValue)) {
                            resp.setStatus(422);
                            return;
                        } else if (Double.parseDouble(filterValue) <= 0) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                    case "x":
                        if (!tryParseDouble(filterValue)) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                    case "y":
                        if (!tryParseLong(filterValue)) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                    case "type":
                        if (!tryParseShort(filterValue)) {
                            resp.setStatus(422);
                            return;
                        }
                    case "street":
                        if (filterValue.isEmpty()) {
                            resp.setStatus(422);
                            return;
                        }
                        break;
                }
                filters.put(filterName, filterValue);
                count++;
            }

            if (count <= mustBe) {
                resp.setStatus(400);
                return;
            }
        }

        count = 0;
        if (paramsMap.get("sortBy") != null) {
            sorts = new LinkedHashMap<>();
            String tmpString = paramsMap.get("sortBy");
            try {
                tmp = tmpString.split(",");
            } catch (Exception e) {
                resp.setStatus(400);
                return;
            }
            String sortName;
            String sortValue;
            mustBe = tmpString.chars().filter(ch -> ch == ',').count();
            for (String sortStr : tmp) {
                try {
                    sortName = URLDecoder.decode(sortStr.substring(0, sortStr.indexOf('!')), "UTF-8");
                    sortValue = URLDecoder.decode(sortStr.substring(sortStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    resp.setStatus(400);
                    return;
                }
                if (!fields.contains(sortName) || !sortBys.contains(sortValue)) {
                    resp.setStatus(422);
                    return;
                }
                sorts.put(sortName, sortValue);
                count++;
            }
            if (count <= mustBe) {
                resp.setStatus(400);
                return;
            }
        }

        if (paramsMap.get("page") != null) {
            try {
                String t = URLDecoder.decode(paramsMap.get("page"), "UTF-8");
                tmp = new String[]{t.substring(0, t.indexOf('!')), t.substring(t.indexOf('!') + 1)};
            } catch (Exception e) {
                resp.setStatus(400);
                return;
            }
            if (tmp.length == 2 && tryParseInt(tmp[0]) && tryParseInt(tmp[1])) {
                pagination = new Page();
                pagination.PageSize = Integer.parseInt(tmp[0]);
                pagination.PageNumber = Integer.parseInt(tmp[1]);
            } else {
                resp.setStatus(422);
                return;
            }
        }
        organizations = repository.getChosenOrganizations(filters, sorts, pagination);
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        writer.write(gson.toJson(organizations));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        repository.addNewOrganization(name, x, y, annualTurnover, type, street);
    }
}