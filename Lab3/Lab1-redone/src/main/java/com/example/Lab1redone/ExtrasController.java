package com.example.Lab1redone;

import Repository.OrganizationRepository;
import ReturnableEntities.AverageAnnualTurnover;
import ReturnableEntities.HigherAnnualTurnovers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;

import static Utils.Utils.*;

@CrossOrigin
@RestController
public class ExtrasController {
    private OrganizationRepository repository;

    public ExtrasController() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getDBString(), getDBUser(), getDBUserPassword());
            repository = new OrganizationRepository(connection);
        } catch (Exception ignored) {
            throw new RuntimeException();
        }
    }

    @GetMapping("/organizations/higher-turnovers")
    public ResponseEntity getHigherTurnovers(@RequestParam("turnover") double turnover) {
        if (turnover == Double.POSITIVE_INFINITY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (turnover < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        HigherAnnualTurnovers higher = new HigherAnnualTurnovers(repository.countHigherAnnualTurnover(turnover));
        return ResponseEntity.ok(higher);
    }

    @GetMapping("/organizations/average-turnover")
    public ResponseEntity getAverage() {
        AverageAnnualTurnover avg = new AverageAnnualTurnover(repository.getAverageAnnualTurnover());
        return ResponseEntity.ok(avg);
    }

    @DeleteMapping("/organizations/equal-turnover")
    public ResponseEntity deleteEqualTurnover(@RequestParam("turnover") double turnover) {
        if (turnover == Double.POSITIVE_INFINITY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (turnover < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        repository.deleteOrganizationsWithEqualTurnover(turnover);
        return ResponseEntity.ok().body(null);
    }
}
