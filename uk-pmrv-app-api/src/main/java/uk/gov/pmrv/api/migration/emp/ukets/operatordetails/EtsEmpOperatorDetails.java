package uk.gov.pmrv.api.migration.emp.ukets.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpOperatorDetails {

    private String etsAccountId;

    //service contact details
    private String serviceContactName;
    private String serviceContactEmail;

    private String operatorName;
    private String crcoCode;
    //flight identification
    private boolean isIcaoDesignator;
    private String icaoDesignator;
    private Set<String> aircraftRegistrationMarkings;
    //Air operating certificate
    private boolean operatingCertificateExist;
    private String aocNumber;
    private String aocIssuingAuthority;
    private String aocStoredFileName;
    private String aocUploadedFileName;
    //Operating license
    private boolean operatingLicenseExist;
    private String operatingLicenseRefNumber;
    private String operatingLicenseIssuingAuthority;
    //Organisation structure
    private String legalStatus;
    //legal status = Company / Limited Liability Partnership
    private boolean registeredCompanyExist;
    private String registeredCompanyRegistrationNumber;
    private String companyRegistrationNumber;

    //Registered address
    private String registeredAddressLine1;
    private String registeredAddressLine2;
    private String registeredCity;
    private String registeredStateProvinceRegion;
    private String registeredPostcode;
    private String registeredCountry;

    //Company address
    private String companyLtdAddressLine1;
    private String companyLtdAddressLine2;
    private String companyLtdCity;
    private String companyLtdStateProvinceRegion;
    private String companyLtdPostcode;
    private String companyLtdCountry;
    //legal status = Individual / Sole Trader
    private String aircraftOperatorContactFirstName;
    private String aircraftOperatorContactSurname;

    //Address
    private String aoAddressLine1;
    private String aoAddressLine2;
    private String aoCity;
    private String aoStateProvinceRegion;
    private String aoPostcode;
    private String aoCountry;
    //legal status = Partnership
    private String partnershipName;
    //Address
    private String partnerAddressLine1;
    private String partnerAddressLine2;
    private String partnerCity;
    private String partnerStateProvinceRegion;
    private String partnerPostcode;
    private String partnerCountry;

    private Set<String> partners;

    //Activities Description
    private String operatorType;
    private String carriedOutFlights;
    private String aviationActivity;
    private String activityDescription;

    //Flights and aircraft monitoring procedures
    //Details about the aircraft in use procedure form
    private String emissionSourcesProcedureTitle;
    private String emissionSourcesProcedureReference;
    private String emissionSourcesProcedureDescription;
    private String emissionSourcesDataMaintenancePost;
    private String emissionSourcesRecordsLocation;
    private String emissionSourcesSystemName;

    //Details about the completeness of the flights list procedure form
    private String flightsListProcedureTitle;
    private String flightsListProcedureReference;
    private String flightsListProcedureDescription;
    private String flightsListDataMaintenancePost;
    private String flightsListRecordsLocation;
    private String flightsListSystemName;

    //Details about flights covered by the UK ETS procedure form
    private String annex1ProcedureTitle;
    private String annex1ProcedureReference;
    private String annex1ProcedureDescription;
    private String annex1DataMaintenancePost;
    private String annex1RecordsLocation;
    private String annex1SystemName;


}
