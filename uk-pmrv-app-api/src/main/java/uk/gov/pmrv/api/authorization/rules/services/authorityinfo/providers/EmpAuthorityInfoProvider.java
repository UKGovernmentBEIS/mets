package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

public interface EmpAuthorityInfoProvider {
    Long getEmpAccountById(String id);
}
