package com.example.Lab1redone;

import Entities.OrganizationModel;
import Entities.Page;
import Repository.OrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

import static Utils.Utils.*;

@CrossOrigin
@RestController
public class MainController {

    private OrganizationRepository repository;

    public MainController() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    @RequestMapping(path = "/organizations", method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(value = "sortBy", required = false) String sortBy,
                              @RequestParam(value = "filterBy", required = false) String filterBy,
                              @RequestParam(value = "page", required = false) String page) {
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            String filterName;
            String filterValue;
            mustBe = filterBy.chars().filter(ch -> ch == ',').count();
            for (String filterStr : tmp) {
                try {
                    filterName = URLDecoder.decode(filterStr.substring(0, filterStr.indexOf('!')), "UTF-8");
                    filterValue = URLDecoder.decode(filterStr.substring(filterStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (!fields.contains(filterName)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                switch (filterName) {
                    case "id":
                        if (!tryParseInt(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Integer.parseInt(filterValue) <= 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "name":
                    case "date":
                        if (filterValue.isEmpty()) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "turnover":
                        if (!tryParseDouble(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Double.parseDouble(filterValue) <= 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "x":
                        if (!tryParseDouble(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "y":
                        if (!tryParseLong(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "type":
                        if (!tryParseShort(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                    case "street":
                        if (filterValue.isEmpty()) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                    case "employees":
                        if (!tryParseLong(filterValue)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        } else if (Long.parseLong(filterValue) < 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                        break;
                }
                filters.put(filterName, filterValue);
                count++;
            }
            if (count <= mustBe) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        count = 0;
        if (sortBy != null) {
            sorts = new LinkedHashMap<>();
            try {
                tmp = sortBy.split(",");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            String sortName;
            String sortValue;
            mustBe = sortBy.chars().filter(ch -> ch == ',').count();
            for (String sortStr : tmp) {
                try {
                    sortName = URLDecoder.decode(sortStr.substring(0, sortStr.indexOf('!')), "UTF-8");
                    sortValue = URLDecoder.decode(sortStr.substring(sortStr.indexOf('!') + 1), "UTF-8");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (!fields.contains(sortName) || !sortBys.contains(sortValue)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                sorts.put(sortName, sortValue);
                count++;
            }
            if (count <= mustBe) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        if (page != null) {
            try {
                String t = URLDecoder.decode(page, "UTF-8");
                tmp = new String[]{t.substring(0, t.indexOf('!')), t.substring(t.indexOf('!') + 1)};
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (tmp.length != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (tryParseInt(tmp[0]) && tryParseInt(tmp[1])) {
                pagination = new Page();
                pagination.PageSize = Integer.parseInt(tmp[0]);
                pagination.PageNumber = Integer.parseInt(tmp[1]);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (pagination.PageSize < 1 && pagination.PageNumber < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        return ResponseEntity.ok(repository.getChosenOrganizations(filters, sorts, pagination));

    }

    @RequestMapping(path = "/organizations", method = RequestMethod.POST)
    public ResponseEntity addOrganization(@RequestBody OrganizationModel organizationModel) {
//        if (repository == null) {
//            repository = new OrganizationRepository();
//        }
        if (organizationModel != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null ||
                    organizationModel.turnover < 1 || organizationModel.name.isEmpty() || organizationModel.x == null ||
                    organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0) ||
                    organizationModel.employees != null && organizationModel.employees < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            repository.addNewOrganization(organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.type, organizationModel.street, organizationModel.employees);
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
