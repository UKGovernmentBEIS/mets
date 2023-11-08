package uk.gov.pmrv.api.migration.emp.corsia.operatordetails.subsidiarycompanies;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia {

	private String fldEmitterID;

    private String operatorName;
    private String subsidiaryAeroplaneOperatorName;
    private String aircraftIdentification;
    private String uniqueIcaoDesignator;
    private Set<String> aeroplaneRegistrationMarkings;
    private boolean operatingCertificateExist;
    private String aocNumber;
    private String aocIssuingAuthority;
    private boolean restrictionExist;
    private String restrictionDetails;
    private String companyRegistrationNumber;
    private String registeredAddressLine1;
    private String registeredAddressLine2;
    private String registeredCity;
    private String registeredStateProvinceRegion;
    private String registeredPostcode;
    private String registeredCountry;
    private String activityType;
    private String activitySummary;
}
