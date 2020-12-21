package com.example.Lab1redone;


import Entities.Organization;
import Entities.OrganizationModel;
import Repository.OrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;

@CrossOrigin
@RestController
public class IdController {
    private OrganizationRepository repository;

    public IdController() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    @GetMapping(path = "/organizations/{id}")
    public ResponseEntity getById(@PathVariable("id") int id) {
        if (id < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            return ResponseEntity.ok(organization);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(path = "/organizations/{id}")
    public ResponseEntity deleteById(@PathVariable("id") int id) {
        if (id < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            repository.deleteOrganization(id);
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping(path = "/organizations/{id}")
    public ResponseEntity updateOrganization(@PathVariable("id") int id, @RequestBody OrganizationModel organizationModel) {
        if (id < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Organization organization = repository.getById(id);
        if (organization != null) {
            if (organizationModel.name == null || organizationModel.turnover == null || organizationModel.y == null ||
                    organizationModel.turnover < 1 || organizationModel.name.isEmpty() || organizationModel.x == null ||
                    organizationModel.type != null && (organizationModel.type > 4 || organizationModel.type < 0) ||
                    organizationModel.employees != null && organizationModel.employees < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            repository.updateOrganization(id, organizationModel.name, organizationModel.x, organizationModel.y, organizationModel.turnover, organizationModel.street, organizationModel.type, organizationModel.employees);
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
