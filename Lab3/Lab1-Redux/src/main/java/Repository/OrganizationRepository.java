package Repository;

import Entities.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;

public class OrganizationRepository {
    private Connection connection;
    private final String defaultSelect = "SELECT \"AnnualTurnover\", \"CreationDate\", \"Id\", \"Name\", \"OrganizationType\", x, y, \"PostalAddress\", \"Employees\" FROM \"Organization\"";
    private final String insert = "INSERT INTO \"Organization\" (\"AnnualTurnover\", \"Name\", \"OrganizationType\", x, y, \"PostalAddress\", \"Employees\") VALUES (?, ?, ?, ?, ?, ?, ?);";
    private final String delete = "DELETE from \"Organization\" where ";

    public OrganizationRepository(Connection connection) {
        this.connection = connection;
    }

    public Organization getById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(defaultSelect + " where  \"Id\"=" + id + ";")) {
            ArrayList<Organization> organizations = getResult(stmt.executeQuery());
            if (organizations.size() == 0)
                return null;
            return organizations.get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Organization> getAllOrganizations() {
        try (PreparedStatement stmt = connection.prepareStatement(defaultSelect + ";")) {
            ArrayList<Organization> organizations = getResult(stmt.executeQuery());
            if (organizations.size() == 0)
                return null;
            return organizations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addNewOrganization(String Name, Double x, Long y, Double annualTurnover, Short organizationType, String street, Long employees) {
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setDouble(1, annualTurnover);
            stmt.setString(2, Name);
            stmt.setLong(5, y);

            if (organizationType == null) {
                stmt.setNull(3, Types.SMALLINT);
            } else {
                stmt.setShort(3, organizationType);
            }

//            if (x == null) {
//                stmt.setNull(4, Types.DOUBLE);
//            } else {
//                stmt.setDouble(4, x);
//            }
            stmt.setDouble(4, x);
            stmt.setString(6, street);
            if (employees == null) {
                stmt.setNull(7, Types.BIGINT);
            } else {
                stmt.setLong(7, employees);
            }
            stmt.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void updateOrganization(Integer id, String name, Double x, Long y, Double annualTurnover, String street, Short type, Long employees) {

        try (PreparedStatement stmt = connection.prepareStatement("UPDATE \"Organization\" SET \"Name\"=?, x=?, y=?, \"AnnualTurnover\"=?, \"PostalAddress\"=?, \"OrganizationType\"=?, \"Employees\"=? WHERE \"Id\"=?")) {
            stmt.setString(1, name);
//            if (x == null) {
//                stmt.setNull(2, Types.DOUBLE);
//            } else {
//                stmt.setDouble(2, x);
//            }
            stmt.setDouble(2, x);

            stmt.setLong(3, y);
            stmt.setDouble(4, annualTurnover);
            stmt.setString(5, street);
            if (type == null) {
                stmt.setNull(6, Types.SMALLINT);
            } else {
                stmt.setShort(6, type);
            }
            if (employees == null) {
                stmt.setNull(7, Types.BIGINT);
            } else {
                stmt.setLong(7, employees);
            }
            stmt.setInt(8, id);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Organization> getChosenOrganizations(Map<String, String> filters, Map<String, String> sorts, Page pagination) {
        StringBuilder bld = new StringBuilder(defaultSelect);
        try {
            if (filters != null) {
                bld.append(" WHERE ");
                int i = 0;
                if (filters.get("id") != null) {
                    bld.append("\"Id\"='").append(filters.get("id")).append("' ");
                    i++;
                }
                if (filters.get("name") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"Name\"='").append(filters.get("name").replace("'", "''")).append("' ");
                    i++;
                }
                if (filters.get("turnover") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"AnnualTurnover\"='").append(filters.get("turnover")).append("' ");
                    i++;
                }
                if (filters.get("date") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"CreationDate\"::text LIKE '%").append(filters.get("date").replace("'", "''")).append("%' ");
                    i++;
                }
                if (filters.get("x") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" x='").append(filters.get("x")).append("' ");
                    i++;
                }
                if (filters.get("y") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" y='").append(filters.get("y")).append("' ");
                    i++;
                }
                if (filters.get("street") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"PostalAddress\"='").append(filters.get("street").replace("'", "''")).append("' ");
                    i++;
                }
                if (filters.get("type") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"OrganizationType\"='").append(filters.get("type")).append("' ");
                    i++;
                }
                if (filters.get("employees") != null) {
                    if (i > 0) {
                        bld.append(" AND ");
                    }
                    bld.append(" \"Employees\"='").append(filters.get("employees")).append("' ");
                }
            }
            if (sorts != null) {
                bld.append(" ORDER BY ");
                int i = 0;
                if (sorts.get("id") != null) {
                    bld.append(" \"Id\" ").append(sorts.get("id"));
                    i++;
                }
                if (sorts.get("name") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"Name\" ").append(sorts.get("name"));
                    i++;
                }
                if (sorts.get("turnover") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"AnnualTurnover\" ").append(sorts.get("turnover"));
                    i++;
                }
                if (sorts.get("date") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"CreationDate\" ").append(sorts.get("date"));
                    i++;
                }
                if (sorts.get("x") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" x ").append(sorts.get("x"));
                    i++;
                }
                if (sorts.get("y") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" y ").append(sorts.get("y"));
                    i++;
                }
                if (sorts.get("street") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"PostalAddress\" ").append(sorts.get("street"));
                    i++;
                }
                if (sorts.get("type") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"OrganizationType\" ").append(sorts.get("type"));
                    i++;
                }
                if (sorts.get("employees") != null) {
                    if (i > 0) {
                        bld.append(" , ");
                    }
                    bld.append(" \"Employees\" ").append(sorts.get("employees"));
                }
            }

            if (pagination != null) {
                bld.append(" LIMIT ").append(pagination.PageSize).append(" offset ").append(pagination.PageNumber * pagination.PageSize);
            }

            bld.append(";");
            PreparedStatement stmt = connection.prepareStatement(bld.toString());
            return getResult(stmt.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Organization> getResult(ResultSet resultSet) throws SQLException {
        ArrayList<Organization> organizationArrayList = new ArrayList<Organization>();
        while (resultSet.next()) {
            Organization organization = new Organization();
            organization.setId(resultSet.getInt("Id"));
            organization.setName(resultSet.getString("Name"));

            String timestamp = resultSet.getString("CreationDate").replace(" ", "T");

            try {
                organization.setCreationDate(ZonedDateTime.parse(timestamp));
            } catch (DateTimeParseException e) {
                LocalDateTime ldt;
                if (timestamp.contains("+03")) {
                    ldt = LocalDateTime.parse(timestamp.split("\\+")[0]);
                } else {
                    ldt = LocalDateTime.parse(timestamp);
                }
                organization.setCreationDate(ldt.atZone(ZoneId.of("+03:00")));
            }

            organization.setAnnualTurnover(resultSet.getDouble("AnnualTurnover"));

            Coordinates coordinates = new Coordinates();
//            coordinates.setX(resultSet.getObject("x") == null ? null : resultSet.getDouble("x"));
            coordinates.setX(resultSet.getDouble("x"));
            coordinates.setY(resultSet.getLong("y"));
            organization.setCoordinates(coordinates);

//            organization.setType(null);
            Object obj = resultSet.getObject("OrganizationType");
            if (obj != null) {
                organization.setType(OrganizationType.values()[resultSet.getShort("OrganizationType")]);
            } else {
                organization.setType(null);
            }

            Address address = new Address();
            address.setStreet(resultSet.getString("PostalAddress"));
            organization.setPostalAddress(address);
            organization.setEmployees(resultSet.getObject("Employees") == null ? null : resultSet.getLong("Employees"));
            organizationArrayList.add(organization);
        }
        return organizationArrayList;
    }

    public Double getAverageAnnualTurnover() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT AVG(\"AnnualTurnover\") FROM \"Organization\";")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getDouble("avg");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteOrganization(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(delete + "\"Id\"=" + id + ";")) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteOrganizationsWithEqualTurnover(double annualTurnover) {
        try (PreparedStatement stmt = connection.prepareStatement(delete + "\"AnnualTurnover\"=" + annualTurnover + ";")) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long countHigherAnnualTurnover(double annualTurnover) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM \"Organization\" WHERE \"AnnualTurnover\" > " + annualTurnover + ";")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getLong("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
