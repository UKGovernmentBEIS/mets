package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpOperatorDetailsCorsia {

	private String fldEmitterID;
	private int empVersion;

    //service contact details
    private String serviceContactName;
    private String serviceContactEmail;

    private String aeroplaneOperatorName;
    //flight identification
    private String flightOptions;
    private String uniqueIcaoDesignator;
    private Set<String> aeroplaneRegistrationMarkings;
    //Air operating certificate
    private boolean operatingCertificateExist;
    private String aocNumber;
    private String aocIssuingAuthority;
    private String aocStoredFileName;
    private String aocUploadedFileName;
    private boolean restrictionExist;
    private String restrictionDetails;
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
    private String activitiesSummary;

    //Flights and aircraft procedures
    //aircraftUsedDetails
    private String emissionSourcesProcedureTitle;
    private String emissionSourcesProcedureReference;
    private String emissionSourcesProcedureDescription;
    private String emissionSourcesDataMaintenancePost;
    private String emissionSourcesRecordsLocation;
    private String emissionSourcesSystemName;
    //flightListCompletenessDetails
    private String flightsListProcedureTitle;
    private String flightsListProcedureReference;
    private String flightsListProcedureDescription;
    private String flightsListDataMaintenancePost;
    private String flightsListRecordsLocation;
    private String flightsListSystemName;
    //operatingStatePairs
    private Set<EmpOperatingStatePairsCorsiaDetails> statePairs;
    //internationalFlightsDetermination
    private String internationalProcedureDetailsProcedureTitle;
    private String internationalProcedureDetailsProcedureReference;
    private String internationalProcedureDetailsProcedureDescription;
    private String internationalProcedureDetailsDataMaintenancePost;
    private String internationalProcedureDetailsRecordsLocation;
    private String internationalProcedureDetailsSystemName;
    //internationalFlightsDeterminationOffset
    private String procedureTitle;
    private String procedureReference;
    private String procedureDescription;
    private String dataMaintenancePost;
    private String recordsLocation;
    private String systemName;
    //internationalFlightsDeterminationNoMonitoring
    private String procedureDetailsNoMonitoringProcedureTitle;
    private String procedureDetailsNoMonitoringProcedureReference;
    private String procedureDetailsNoMonitoringProcedureDescription;
    private String procedureDetailsNoMonitoringDataMaintenancePost;
    private String procedureDetailsNoMonitoringRecordsLocation;
    private String procedureDetailsNoMonitoringSystemName;
}
