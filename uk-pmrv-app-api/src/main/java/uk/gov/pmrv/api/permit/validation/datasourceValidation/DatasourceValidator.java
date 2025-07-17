package uk.gov.pmrv.api.permit.validation.datasourceValidation;

public interface DatasourceValidator<T> {
    boolean validateDataSources(T subInstallationMember);
}
