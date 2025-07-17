package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationAccountCountriesMapper;
import uk.gov.pmrv.api.migration.emp.corsia.operatordetails.subsidiarycompanies.EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EmpOperatorDetailsCorsiaMigrationMapper {
	
	private static final String N_A = "N/A";

    public static Map<String, EmpOperatorDetailsFlightIdentificationCorsia> toOperatorDetailsIdentifications(List<EtsEmpOperatorDetailsCorsia> empOperatorDetails) {

        Map<String, List<EtsFileAttachment>> etsFileAttachments = createEtsFileAttachments(empOperatorDetails);

        final Map<String, EmpOperatorDetailsFlightIdentificationCorsia> operatorDetailsFlightIdentificationMap = empOperatorDetails.stream()
                .collect(Collectors.toMap(EtsEmpOperatorDetailsCorsia::getFldEmitterID,
                		EmpOperatorDetailsCorsiaMigrationMapper::toOperatorDetailsIdentification));

        operatorDetailsFlightIdentificationMap.forEach((key,value) -> value.setAttachments(etsFileAttachments));
        return operatorDetailsFlightIdentificationMap;
    }
    
    public static List<SubsidiaryCompanyCorsia> toSubsidiaryCompanies(
    		List<EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia> corsiaSubsidiaryCompanies) {

    	List<SubsidiaryCompanyCorsia> subsidiaryCompanies = new ArrayList<>();
		
    	corsiaSubsidiaryCompanies.forEach(entry -> subsidiaryCompanies.add(
    			SubsidiaryCompanyCorsia.builder()
    			.operatorName(entry.getSubsidiaryAeroplaneOperatorName())
    			.flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType("Option 1".equalsIgnoreCase(entry.getAircraftIdentification()) 
                        		? FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION 
                        				: FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .icaoDesignators(entry.getUniqueIcaoDesignator())
                        .aircraftRegistrationMarkings(entry.getAeroplaneRegistrationMarkings())
                        .build())
    			.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(entry.isOperatingCertificateExist())
                        .certificateNumber(entry.getAocNumber())
                        .issuingAuthority(entry.getAocIssuingAuthority())
                        .restrictionsExist(entry.isRestrictionExist())
                        .restrictionsDetails(entry.getRestrictionDetails())
                        .build())
    			.companyRegistrationNumber(entry.getCompanyRegistrationNumber())
    			.registeredLocation(LocationOnShoreStateDTO.builder()
    					.type(LocationType.ONSHORE_STATE)
    					.line1(entry.getRegisteredAddressLine1())
    					.line2(entry.getRegisteredAddressLine2())
    					.city(entry.getRegisteredCity())
    					.state(entry.getRegisteredStateProvinceRegion())
    					.postcode(entry.getRegisteredPostcode())
    					.country(resolvePmrvCountryFromEtswap(entry.getRegisteredCountry()))
    					.build())
    			.flightTypes(resolveFlightTypes(entry.getActivityType()))
                .activityDescription(entry.getActivitySummary())
                .build()));
		
		return subsidiaryCompanies;
    }

    private static EmpOperatorDetailsFlightIdentificationCorsia toOperatorDetailsIdentification(EtsEmpOperatorDetailsCorsia operatorDetails) {

        return EmpOperatorDetailsFlightIdentificationCorsia.builder()
                .operatorDetails(toOperatorDetails(operatorDetails))
                .flightAndAircraftProcedures(toFlightAndAircraftProcedures(operatorDetails))
                .serviceContactDetails(ServiceContactDetails.builder()
                        .name(operatorDetails.getServiceContactName())
                        .email(operatorDetails.getServiceContactEmail())
                        .roleCode(N_A)
                        .build())
                .empVersion(operatorDetails.getEmpVersion())
                .build();
    }


    private static EmpCorsiaOperatorDetails toOperatorDetails(EtsEmpOperatorDetailsCorsia operatorDetails) {

        return EmpCorsiaOperatorDetails.builder()
                .operatorName(operatorDetails.getAeroplaneOperatorName())
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType("ICAO_DESIGNATORS".equalsIgnoreCase(operatorDetails.getFlightOptions()) 
                        		? FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION 
                        				: FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .icaoDesignators("ICAO_DESIGNATORS".equalsIgnoreCase(operatorDetails.getFlightOptions()) 
                        		? operatorDetails.getUniqueIcaoDesignator() 
                        				: null)
                        .aircraftRegistrationMarkings("REGISTRATION_MARKINGS".equalsIgnoreCase(operatorDetails.getFlightOptions()) 
                        		? operatorDetails.getAeroplaneRegistrationMarkings() 
                        				: null)
                        .build())
                .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(operatorDetails.isOperatingCertificateExist())
                        .certificateNumber(operatorDetails.getAocNumber())
                        .issuingAuthority(operatorDetails.getAocIssuingAuthority())
                        .restrictionsExist(operatorDetails.isRestrictionExist())
                        .restrictionsDetails(operatorDetails.getRestrictionDetails())
                        .build())
                .organisationStructure(resolveOrganisationStructure(operatorDetails))
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                        .operatorType("Commercial".equalsIgnoreCase(operatorDetails.getOperatorType()) ? OperatorType.COMMERCIAL : OperatorType.NON_COMMERCIAL)
                        .flightTypes(resolveFlightTypes(operatorDetails.getCarriedOutFlights()))
                        .activityDescription(operatorDetails.getActivitiesSummary())
                        .build())
                .subsidiaryCompanyExist(false)
                .build();
    }

    private static EmpFlightAndAircraftProceduresCorsia toFlightAndAircraftProcedures(EtsEmpOperatorDetailsCorsia operatorDetails) {

        return EmpFlightAndAircraftProceduresCorsia.builder()
                .aircraftUsedDetails(EmpProcedureForm.builder()
                        .procedureDocumentName(operatorDetails.getEmissionSourcesProcedureTitle())
                        .procedureReference(operatorDetails.getEmissionSourcesProcedureReference())
                        .procedureDescription(operatorDetails.getEmissionSourcesProcedureDescription())
                        .responsibleDepartmentOrRole(operatorDetails.getEmissionSourcesDataMaintenancePost())
                        .locationOfRecords(operatorDetails.getEmissionSourcesRecordsLocation())
                        .itSystemUsed(operatorDetails.getEmissionSourcesSystemName())
                        .build())
                .flightListCompletenessDetails(EmpProcedureForm.builder()
                        .procedureDocumentName(operatorDetails.getFlightsListProcedureTitle())
                        .procedureReference(operatorDetails.getFlightsListProcedureReference())
                        .procedureDescription(operatorDetails.getFlightsListProcedureDescription())
                        .responsibleDepartmentOrRole(operatorDetails.getFlightsListDataMaintenancePost())
                        .locationOfRecords(operatorDetails.getFlightsListRecordsLocation())
                        .itSystemUsed(operatorDetails.getFlightsListSystemName())
                        .build())
                .internationalFlightsDetermination(EmpProcedureForm.builder()
                        .procedureDocumentName(operatorDetails.getInternationalProcedureDetailsProcedureTitle())
                        .procedureReference(operatorDetails.getInternationalProcedureDetailsProcedureReference())
                        .procedureDescription(operatorDetails.getInternationalProcedureDetailsProcedureDescription())
                        .responsibleDepartmentOrRole(operatorDetails.getInternationalProcedureDetailsDataMaintenancePost())
                        .locationOfRecords(operatorDetails.getInternationalProcedureDetailsRecordsLocation())
                        .itSystemUsed(operatorDetails.getInternationalProcedureDetailsSystemName())
                        .build())
                .operatingStatePairs(EmpOperatingStatePairsCorsia.builder()
                		.operatingStatePairsCorsiaDetails(operatorDetails.getStatePairs())
                		.build())
                .internationalFlightsDeterminationOffset(EmpProcedureForm.builder()
                        .procedureDocumentName(operatorDetails.getProcedureTitle())
                        .procedureReference(operatorDetails.getProcedureReference())
                        .procedureDescription(operatorDetails.getProcedureDescription())
                        .responsibleDepartmentOrRole(operatorDetails.getDataMaintenancePost())
                        .locationOfRecords(operatorDetails.getRecordsLocation())
                        .itSystemUsed(operatorDetails.getSystemName())
                        .build())
                .internationalFlightsDeterminationNoMonitoring(EmpProcedureForm.builder()
                        .procedureDocumentName(operatorDetails.getProcedureDetailsNoMonitoringProcedureTitle())
                        .procedureReference(operatorDetails.getProcedureDetailsNoMonitoringProcedureReference())
                        .procedureDescription(operatorDetails.getProcedureDetailsNoMonitoringProcedureDescription())
                        .responsibleDepartmentOrRole(operatorDetails.getProcedureDetailsNoMonitoringDataMaintenancePost())
                        .locationOfRecords(operatorDetails.getProcedureDetailsNoMonitoringRecordsLocation())
                        .itSystemUsed(operatorDetails.getProcedureDetailsNoMonitoringSystemName())
                        .build())
                .build();
    }

    private static OrganisationStructure resolveOrganisationStructure(EtsEmpOperatorDetailsCorsia operatorDetails) {
        if ("Company / Limited Liability Partnership".equalsIgnoreCase(operatorDetails.getLegalStatus())) {
            String companyLtdCountry = resolvePmrvCountryFromEtswap(operatorDetails.getCompanyLtdCountry());
            String registeredCountry = resolvePmrvCountryFromEtswap(operatorDetails.getRegisteredCountry());

            LocationOnShoreStateDTO organisationLocation;

            if(operatorDetails.isRegisteredCompanyExist()) {
                organisationLocation = LocationOnShoreStateDTO.builder()
                        .type(LocationType.ONSHORE_STATE)
                        .line1(operatorDetails.getRegisteredAddressLine1())
                        .line2(operatorDetails.getRegisteredAddressLine2())
                        .city(operatorDetails.getRegisteredCity())
                        .country(registeredCountry)
                        .postcode(operatorDetails.getRegisteredPostcode())
                        .state(operatorDetails.getRegisteredStateProvinceRegion())
                        .build();
            } else {
                organisationLocation = LocationOnShoreStateDTO.builder()
                        .type(LocationType.ONSHORE_STATE)
                        .line1(operatorDetails.getCompanyLtdAddressLine1())
                        .line2(operatorDetails.getCompanyLtdAddressLine2())
                        .city(operatorDetails.getCompanyLtdCity())
                        .country(companyLtdCountry)
                        .postcode(operatorDetails.getCompanyLtdPostcode())
                        .state(operatorDetails.getCompanyLtdStateProvinceRegion())
                        .build();
            }

            return LimitedCompanyOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                    .organisationLocation(organisationLocation)
                    .differentContactLocationExist(operatorDetails.isRegisteredCompanyExist())
                    .registrationNumber(operatorDetails.isRegisteredCompanyExist() ? operatorDetails.getRegisteredCompanyRegistrationNumber() :
                            operatorDetails.getCompanyRegistrationNumber())
                    .differentContactLocation(operatorDetails.isRegisteredCompanyExist() ? LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(operatorDetails.getCompanyLtdAddressLine1())
                            .line2(operatorDetails.getCompanyLtdAddressLine2())
                            .city(operatorDetails.getCompanyLtdCity())
                            .country(companyLtdCountry)
                            .postcode(operatorDetails.getCompanyLtdPostcode())
                            .state(operatorDetails.getCompanyLtdStateProvinceRegion())
                            .build() : null)
                    .build();
        } else if ("Individual / Sole Trader".equalsIgnoreCase(operatorDetails.getLegalStatus())) {
            String aoCountry = resolvePmrvCountryFromEtswap(operatorDetails.getAoCountry());

            return IndividualOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                    .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(operatorDetails.getAoAddressLine1())
                            .line2(operatorDetails.getAoAddressLine2())
                            .city(operatorDetails.getAoCity())
                            .country(aoCountry)
                            .postcode(operatorDetails.getAoPostcode())
                            .state(operatorDetails.getAoStateProvinceRegion())
                            .build())
                    .fullName(operatorDetails.getAircraftOperatorContactFirstName() + " " + operatorDetails.getAircraftOperatorContactSurname())
                    .build();
        } else {
            String partnerCountry = resolvePmrvCountryFromEtswap(operatorDetails.getPartnerCountry());

            return PartnershipOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                    .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(operatorDetails.getPartnerAddressLine1())
                            .line2(operatorDetails.getPartnerAddressLine2())
                            .city(operatorDetails.getPartnerCity())
                            .country(partnerCountry)
                            .postcode(operatorDetails.getPartnerPostcode())
                            .state(operatorDetails.getPartnerStateProvinceRegion())
                            .build())
                    .partnershipName(operatorDetails.getPartnershipName())
                    .partners(operatorDetails.getPartners())
                    .build();
        }
    }

    private static Set<FlightType> resolveFlightTypes(String carriedOutFlights) {
        if ("Scheduled flights".equalsIgnoreCase(carriedOutFlights)) {
            return Set.of(FlightType.SCHEDULED);
        } else if ("Non-scheduled flights".equalsIgnoreCase(carriedOutFlights)) {
            return Set.of(FlightType.NON_SCHEDULED);
        } else return Set.of(FlightType.SCHEDULED, FlightType.NON_SCHEDULED);
    }

    private static Map<String, List<EtsFileAttachment>> createEtsFileAttachments(List<EtsEmpOperatorDetailsCorsia> empOperatorDetails) {
        return empOperatorDetails.stream().map(operatorDetail -> (operatorDetail.getAocStoredFileName()!=null && operatorDetail.getAocUploadedFileName()!=null) ?
                        EtsFileAttachment.builder()
                                .etsAccountId(operatorDetail.getFldEmitterID())
                                .uploadedFileName(operatorDetail.getAocUploadedFileName())
                                .storedFileName(operatorDetail.getAocStoredFileName())
                                .uuid(UUID.randomUUID())
                                .build() : null)
                .filter(Objects::nonNull)
                .filter(etsFileAttachment -> MigrationConstants.ALLOWED_FILE_TYPES.contains(etsFileAttachment.getUploadedFileName().substring(etsFileAttachment.getUploadedFileName().lastIndexOf(".")).toLowerCase()))
                .collect(Collectors.groupingBy(EtsFileAttachment::getEtsAccountId));
    }

    private static String resolvePmrvCountryFromEtswap(String country) {
        Map<String, String> countriesMapping = AviationAccountCountriesMapper.getEtswapToPmrvCountriesMapping();

        if(ObjectUtils.isNotEmpty(country) && countriesMapping.containsKey(country)) {
            return countriesMapping.get(country);
        }

        return country;
    }
}
