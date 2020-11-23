package Servlets;

import Entities.OrganizationModel;
import Entities.Page;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static Utils.Utils.*;

@Path("/organizations")
public class MainController {
    public MainController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Encoded @QueryParam("sortBy") String sortBy,
                        @Encoded @QueryParam("filterBy") String filterBy,
                        @Encoded @QueryParam("page") String page,
                        @Context UriInfo uriInfo) {
        String[] tmp = null;
        Map<String, String> filters = null;
        Map<String, String> sorts = null;
        Page pagination = null;
        long mustBe = 0;
        long count = 0;
        if (filterBy != null) {
            filters = new LinkedHashMap<>();
            try {
                tmp = filterBy.split(",");
            } catch (Exception e) {
                return Response.status(400).build();

            }
            String filterName;
            String filterValue;
            mustBe = filterBy.chars().filter(ch -> ch == ',').count();
            for (String filterStr : tmp) {
                try {
                    filterName = URLDecoder.decode(filterStr.substring(0, filterStr.indexOf('!')), "UTF-8");
                    filterValue = URLDecoder.decode(filterStr.substring(filterStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    return Response.status(400).build();
                }
                if (!fields.contains(filterName)) {
                    return Response.status(400).build();
                }
                switch (filterName) {
                    case "id":
                        if (!tryParseInt(filterValue)) {
                            return Response.status(400).build();
                        } else if (Integer.parseInt(filterValue) <= 0) {
                            return Response.status(400).build();
                        }
                        break;
                    case "name":
                    case "date":
                        if (filterValue.isEmpty()) {
                            return Response.status(400).build();
                        }
                        break;
                    case "turnover":
                        if (!tryParseDouble(filterValue)) {
                            return Response.status(400).build();
                        } else if (Double.parseDouble(filterValue) <= 0) {
                            return Response.status(400).build();
                        }
                        break;
                    case "x":
                        if (!tryParseDouble(filterValue)) {
                            return Response.status(400).build();
                        }
                        break;
                    case "y":
                        if (!tryParseLong(filterValue)) {
                            return Response.status(400).build();
                        }
                        break;
                    case "type":
                        if (!tryParseShort(filterValue)) {
                            return Response.status(400).build();
                        }
                    case "street":
                        if (filterValue.isEmpty()) {
                            return Response.status(400).build();
                        }
                        break;
                }
                filters.put(filterName, filterValue);
                count++;
            }
            if (count <= mustBe) {
                return Response.status(400).build();
            }
        }

        count = 0;
        if (sortBy != null) {
            sorts = new LinkedHashMap<>();
            try {
                tmp = sortBy.split(",");
            } catch (Exception e) {
                return Response.status(400).build();
            }
            String sortName;
            String sortValue;
            mustBe = sortBy.chars().filter(ch -> ch == ',').count();
            for (String sortStr : tmp) {
                try {
                    sortName = URLDecoder.decode(sortStr.substring(0, sortStr.indexOf('!')), "UTF-8");
                    sortValue = URLDecoder.decode(sortStr.substring(sortStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    return Response.status(400).build();
                }
                if (!fields.contains(sortName) || !sortBys.contains(sortValue)) {
                    return Response.status(400).build();
                }
                sorts.put(sortName, sortValue);
                count++;
            }
            if (count <= mustBe) {
                return Response.status(400).build();
            }
        }

        if (page != null) {
            try {
                String t = URLDecoder.decode(page, "UTF-8");
                tmp = new String[]{t.substring(0, t.indexOf('!')), t.substring(t.indexOf('!') + 1)};
            } catch (Exception e) {
                return Response.status(400).build();
            }
            if (tmp.length != 2) {
                return Response.status(400).build();
            }
            if (tryParseInt(tmp[0]) && tryParseInt(tmp[1])) {
                pagination = new Page();
                pagination.PageSize = Integer.parseInt(tmp[0]);
                pagination.PageNumber = Integer.parseInt(tmp[1]);
            } else {
                return Response.status(400).build();
            }
            if (pagination.PageSize < 1 && pagination.PageNumber < 0) {
                return Response.status(400).build();
            }
        }
        return Response.ok(repository.getChosenOrganizations(filters, sorts, pagination)).build();
    }

    @POST
    public Response addOrganization(OrganizationModel organizationModel) {
        if (organizationModel != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null) {
                return Response.status(400).build();
            }
            if (organizationModel.turnover < 1 || organizationModel.name.isEmpty()) {
                return Response.status(400).build();
            }
            if (organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0)) {
                return Response.status(400).build();
            }
            repository.addNewOrganization(organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.type, organizationModel.street);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}