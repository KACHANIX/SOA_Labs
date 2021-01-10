package Default;

import Entities.Organization;
import Entities.OrganizationModel;
import Entities.Page;
import Repository.OrganizationRepository;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static Utils.Utils.*;

@WebService(
//        endpointInterface = "Default.Service",
        name = "Service",
        serviceName = "Service"
//        portName = "ServicePort"
)
public class ServiceImpl implements Service {
    private OrganizationRepository repository;

    public ServiceImpl() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    //region EXTRAS
    @Override
    public Long getHigherTurnovers(double turnover) throws Exception {
        if (turnover == Double.POSITIVE_INFINITY) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (turnover < 1) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return repository.countHigherAnnualTurnover(turnover);
    }

    @Override
    public Double getAverage() {
        return repository.getAverageAnnualTurnover();
    }

    @Override
    public void deleteEqualTurnover(double turnover) throws Exception {
        if (turnover == Double.POSITIVE_INFINITY) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (turnover < 1) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        repository.deleteOrganizationsWithEqualTurnover(turnover);
    }
    //endregion

    // region ID
    @Override
    public Organization getById(int id) throws Exception {
        if (id < 1) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            return organization;
        } else {
            throw new Exception("Not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public void deleteById(int id) throws Exception {
        if (id < 1) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            repository.deleteOrganization(id);
//            return ResponseEntity.ok().body(null);
        } else {
            throw new Exception("Not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public void updateOrganization(int id, OrganizationModel organizationModel) throws Exception {
        if (id < 1) {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null ||
                    organizationModel.turnover < 1 || organizationModel.name.isEmpty() || organizationModel.x == null ||
                    organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0) ||
                    organizationModel.employees != null && organizationModel.employees < 0) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            repository.updateOrganization(id, organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.street, organizationModel.type, organizationModel.employees);
//            return ResponseEntity.ok().body(null);
        } else {
            throw new Exception("Not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    //endregion

    //region MAIN

    @Override
    public void addOrganization(OrganizationModel organizationModel) throws Exception {
        if (organizationModel != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null ||
                    organizationModel.turnover < 1 || organizationModel.name.isEmpty() || organizationModel.x == null ||
                    organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0) ||
                    organizationModel.employees != null && organizationModel.employees < 0) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            repository.addNewOrganization(organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.type, organizationModel.street, organizationModel.employees);
//            return ResponseEntity.ok().body(null);
        } else {
            throw new Exception("Bad request");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Override
    public ArrayList<Organization> getOrganizations(String sortBy, String filterBy, String page) throws Exception {
        String[] tmp = null;
        Map<String, String> filters = null;
        Map<String, String> sorts = null;
        Page pagination = null;
        long mustBe = 0;
        long count = 0;
        if (filterBy != null ) {
            filters = new LinkedHashMap<>();
            try {
                tmp = filterBy.split(",");
            } catch (Exception e) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            String filterName;
            String filterValue;
            mustBe = filterBy.chars().filter(ch -> ch == ',').count();
            for (String filterStr : tmp) {
                try {
                    filterName = URLDecoder.decode(filterStr.substring(0, filterStr.indexOf('!')), "UTF-8");
                    filterValue = URLDecoder.decode(filterStr.substring(filterStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    throw new Exception("Bad request");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (!fields.contains(filterName)) {
                    throw new Exception("Bad request");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                switch (filterName) {
                    case "id":
                        if (!tryParseInt(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Integer.parseInt(filterValue) <= 0) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "name":
                    case "date":
                        if (filterValue.isEmpty()) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "turnover":
                        if (!tryParseDouble(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Double.parseDouble(filterValue) <= 0) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "x":
                        if (!tryParseDouble(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "y":
                        if (!tryParseLong(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "type":
                        if (!tryParseShort(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                    case "street":
                        if (filterValue.isEmpty()) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "employees":
                        if (!tryParseLong(filterValue)) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Long.parseLong(filterValue) < 0) {
                            throw new Exception("Bad request");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                }
                filters.put(filterName, filterValue);
                count++;
            }
            if (count <= mustBe) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        count = 0;
        if (sortBy != null) {
            sorts = new LinkedHashMap<>();
            try {
                tmp = sortBy.split(",");
            } catch (Exception e) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            String sortName;
            String sortValue;
            mustBe = sortBy.chars().filter(ch -> ch == ',').count();
            for (String sortStr : tmp) {
                try {
                    sortName = URLDecoder.decode(sortStr.substring(0, sortStr.indexOf('!')), "UTF-8");
                    sortValue = URLDecoder.decode(sortStr.substring(sortStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    throw new Exception("Bad request");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (!fields.contains(sortName) || !sortBys.contains(sortValue)) {
                    throw new Exception("Bad request");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                sorts.put(sortName, sortValue);
                count++;
            }
            if (count <= mustBe) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        if (page != null) {
            try {
                String t = URLDecoder.decode(page, "UTF-8");
                tmp = new String[]{t.substring(0, t.indexOf('!')), t.substring(t.indexOf('!') + 1)};
            } catch (Exception e) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (tmp.length != 2) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (tryParseInt(tmp[0]) && tryParseInt(tmp[1])) {
                pagination = new Page();
                pagination.PageSize = Integer.parseInt(tmp[0]);
                pagination.PageNumber = Integer.parseInt(tmp[1]);
            } else {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (pagination.PageSize < 1 && pagination.PageNumber < 0) {
                throw new Exception("Bad request");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return repository.getChosenOrganizations(filters, sorts, pagination);
//        return ResponseEntity.ok(repository.getChosenOrganizations(filters, sorts, pagination));
    }

    //endregion
}
