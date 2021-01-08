package Default;

import Entities.Organization;
import Entities.OrganizationModel;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;

@WebService
public interface Service {
    // region EXTRAS
    @WebMethod
    Long getHigherTurnovers(double turnover) throws Exception;

    @WebMethod
    Double getAverage();

    @WebMethod
    void deleteEqualTurnover(double turnover) throws Exception;
    //endregion

    // region ID
    @WebMethod
    Organization getById(int id) throws Exception;

    @WebMethod
    void deleteById(int id) throws Exception;

    @WebMethod
    void updateOrganization(int id, OrganizationModel organizationModel) throws Exception;
    //endregion

    // region MAIN

    @WebMethod
    ArrayList<Organization> getOrganizations(String sortBy, String filterBy, String page) throws Exception;

    @WebMethod
    void addOrganization(OrganizationModel organizationModel) throws Exception;
    //endregion

}

