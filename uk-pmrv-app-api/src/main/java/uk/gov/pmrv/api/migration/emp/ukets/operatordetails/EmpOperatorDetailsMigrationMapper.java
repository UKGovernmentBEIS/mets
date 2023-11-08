package uk.gov.pmrv.api.migration.emp.ukets.operatordetails;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationAccountCountriesMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EmpOperatorDetailsMigrationMapper {

    private static final String N_A = "N/A";

    public static Map<String, EmpOperatorDetailsFlightIdentification> toOperatorDetailsIdentifications(List<EtsEmpOperatorDetails> etsEmpOperatorDetails) {

        Map<String, List<EtsFileAttachment>> etsFileAttachments = createEtsFileAttachments(etsEmpOperatorDetails);

        final Map<String, EmpOperatorDetailsFlightIdentification> operatorDetailsFlightIdentificationMap = etsEmpOperatorDetails.stream()
                .collect(Collectors.toMap(EtsEmpOperatorDetails::getEtsAccountId,
                        EmpOperatorDetailsMigrationMapper::toOperatorDetailsIdentification));

        operatorDetailsFlightIdentificationMap.forEach((key,value) -> value.setAttachments(etsFileAttachments));
        return operatorDetailsFlightIdentificationMap;
    }

    private static EmpOperatorDetailsFlightIdentification toOperatorDetailsIdentification(EtsEmpOperatorDetails etsOperatorDetails) {

        return EmpOperatorDetailsFlightIdentification.builder()
                .operatorDetails(toOperatorDetails(etsOperatorDetails))
                .flightAndAircraftProcedures(toFlightAndAircraftProcedures(etsOperatorDetails))
                .serviceContactDetails(ServiceContactDetails.builder()
                        .name(etsOperatorDetails.getServiceContactName())
                        .email(etsOperatorDetails.getServiceContactEmail())
                        .roleCode(N_A)
                        .build())
                .build();
    }


    private static EmpOperatorDetails toOperatorDetails(EtsEmpOperatorDetails etsOperatorDetails) {

        return EmpOperatorDetails.builder()
                .operatorName(etsOperatorDetails.getOperatorName())
                .crcoCode(etsOperatorDetails.getCrcoCode())
                .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(etsOperatorDetails.isIcaoDesignator() ? FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION :
                                FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .icaoDesignators(etsOperatorDetails.getIcaoDesignator())
                        .aircraftRegistrationMarkings(etsOperatorDetails.getAircraftRegistrationMarkings())
                        .build())
                .airOperatingCertificate(AirOperatingCertificate.builder()
                        .certificateExist(etsOperatorDetails.isOperatingCertificateExist())
                        .certificateNumber(etsOperatorDetails.getAocNumber())
                        .issuingAuthority("Turkey - Directorate General of Civil Aviation".equals(etsOperatorDetails.getAocIssuingAuthority())
                                ? "Türkiye - Directorate General of Civil Aviation"
                                : etsOperatorDetails.getAocIssuingAuthority())
                        .build())
                .operatingLicense(OperatingLicense.builder()
                        .licenseExist(etsOperatorDetails.isOperatingLicenseExist())
                        .licenseNumber(etsOperatorDetails.getOperatingLicenseRefNumber())
                        .issuingAuthority("Turkey - Directorate General of Civil Aviation".equals(etsOperatorDetails.getOperatingLicenseIssuingAuthority())
                                ? "Türkiye - Directorate General of Civil Aviation"
                                : etsOperatorDetails.getOperatingLicenseIssuingAuthority())
                        .build())
                .organisationStructure(resolveOrganisationStructure(etsOperatorDetails))
                .activitiesDescription(ActivitiesDescription.builder()
                        .operatorType("Commercial".equalsIgnoreCase(etsOperatorDetails.getOperatorType()) ? OperatorType.COMMERCIAL : OperatorType.NON_COMMERCIAL)
                        .flightTypes(resolveFlightTypes(etsOperatorDetails.getCarriedOutFlights()))
                        .operationScopes(resolveOperationScopes(etsOperatorDetails.getAviationActivity()))
                        .activityDescription(etsOperatorDetails.getActivityDescription())
                        .build())
                .build();
    }

    private static EmpFlightAndAircraftProcedures toFlightAndAircraftProcedures(EtsEmpOperatorDetails etsOperatorDetails) {

        return EmpFlightAndAircraftProcedures.builder()
                .aircraftUsedDetails(EmpProcedureForm.builder()
                        .procedureDocumentName(etsOperatorDetails.getEmissionSourcesProcedureTitle())
                        .procedureReference(etsOperatorDetails.getEmissionSourcesProcedureReference())
                        .procedureDescription(etsOperatorDetails.getEmissionSourcesProcedureDescription())
                        .responsibleDepartmentOrRole(etsOperatorDetails.getEmissionSourcesDataMaintenancePost())
                        .locationOfRecords(etsOperatorDetails.getEmissionSourcesRecordsLocation())
                        .itSystemUsed(etsOperatorDetails.getEmissionSourcesSystemName())
                        .build())
                .flightListCompletenessDetails(EmpProcedureForm.builder()
                        .procedureDocumentName(etsOperatorDetails.getFlightsListProcedureTitle())
                        .procedureReference(etsOperatorDetails.getFlightsListProcedureReference())
                        .procedureDescription(etsOperatorDetails.getFlightsListProcedureDescription())
                        .responsibleDepartmentOrRole(etsOperatorDetails.getFlightsListDataMaintenancePost())
                        .locationOfRecords(etsOperatorDetails.getFlightsListRecordsLocation())
                        .itSystemUsed(etsOperatorDetails.getFlightsListSystemName())
                        .build())
                .ukEtsFlightsCoveredDetails(EmpProcedureForm.builder()
                        .procedureDocumentName(etsOperatorDetails.getAnnex1ProcedureTitle())
                        .procedureReference(etsOperatorDetails.getAnnex1ProcedureReference())
                        .procedureDescription(etsOperatorDetails.getAnnex1ProcedureDescription())
                        .responsibleDepartmentOrRole(etsOperatorDetails.getAnnex1DataMaintenancePost())
                        .locationOfRecords(etsOperatorDetails.getAnnex1RecordsLocation())
                        .itSystemUsed(etsOperatorDetails.getAnnex1SystemName())
                        .build())
                .build();
    }

    private static OrganisationStructure resolveOrganisationStructure(EtsEmpOperatorDetails etsOperatorDetails) {
        if ("Company / Limited Liability Partnership".equalsIgnoreCase(etsOperatorDetails.getLegalStatus())) {
            String companyLtdCountry = resolvePmrvCountryFromEtswap(etsOperatorDetails.getCompanyLtdCountry());
            String registeredCountry = resolvePmrvCountryFromEtswap(etsOperatorDetails.getRegisteredCountry());

            LocationOnShoreStateDTO organisationLocation;

            if(etsOperatorDetails.isRegisteredCompanyExist()) {
                organisationLocation = LocationOnShoreStateDTO.builder()
                        .type(LocationType.ONSHORE_STATE)
                        .line1(etsOperatorDetails.getRegisteredAddressLine1())
                        .line2(etsOperatorDetails.getRegisteredAddressLine2())
                        .city(etsOperatorDetails.getRegisteredCity())
                        .country(registeredCountry)
                        .postcode(etsOperatorDetails.getRegisteredPostcode())
                        .state(etsOperatorDetails.getRegisteredStateProvinceRegion())
                        .build();
            } else {
                organisationLocation = LocationOnShoreStateDTO.builder()
                        .type(LocationType.ONSHORE_STATE)
                        .line1(etsOperatorDetails.getCompanyLtdAddressLine1())
                        .line2(etsOperatorDetails.getCompanyLtdAddressLine2())
                        .city(etsOperatorDetails.getCompanyLtdCity())
                        .country(companyLtdCountry)
                        .postcode(etsOperatorDetails.getCompanyLtdPostcode())
                        .state(etsOperatorDetails.getCompanyLtdStateProvinceRegion())
                        .build();
            }

            return LimitedCompanyOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                    .organisationLocation(organisationLocation)
                    .differentContactLocationExist(etsOperatorDetails.isRegisteredCompanyExist())
                    .registrationNumber(etsOperatorDetails.isRegisteredCompanyExist() ? etsOperatorDetails.getRegisteredCompanyRegistrationNumber() :
                            etsOperatorDetails.getCompanyRegistrationNumber())
                    .differentContactLocation(etsOperatorDetails.isRegisteredCompanyExist() ? LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(etsOperatorDetails.getCompanyLtdAddressLine1())
                            .line2(etsOperatorDetails.getCompanyLtdAddressLine2())
                            .city(etsOperatorDetails.getCompanyLtdCity())
                            .country(companyLtdCountry)
                            .postcode(etsOperatorDetails.getCompanyLtdPostcode())
                            .state(etsOperatorDetails.getCompanyLtdStateProvinceRegion())
                            .build() : null)
                    .build();
        } else if ("Individual / Sole Trader".equalsIgnoreCase(etsOperatorDetails.getLegalStatus())) {
            String aoCountry = resolvePmrvCountryFromEtswap(etsOperatorDetails.getAoCountry());

            return IndividualOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                    .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(etsOperatorDetails.getAoAddressLine1())
                            .line2(etsOperatorDetails.getAoAddressLine2())
                            .city(etsOperatorDetails.getAoCity())
                            .country(aoCountry)
                            .postcode(etsOperatorDetails.getAoPostcode())
                            .state(etsOperatorDetails.getAoStateProvinceRegion())
                            .build())
                    .fullName(etsOperatorDetails.getAircraftOperatorContactFirstName() + " " + etsOperatorDetails.getAircraftOperatorContactSurname())
                    .build();
        } else {
            String partnerCountry = resolvePmrvCountryFromEtswap(etsOperatorDetails.getPartnerCountry());

            return PartnershipOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                    .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1(etsOperatorDetails.getPartnerAddressLine1())
                            .line2(etsOperatorDetails.getPartnerAddressLine2())
                            .city(etsOperatorDetails.getPartnerCity())
                            .country(partnerCountry)
                            .postcode(etsOperatorDetails.getPartnerPostcode())
                            .state(etsOperatorDetails.getPartnerStateProvinceRegion())
                            .build())
                    .partnershipName(etsOperatorDetails.getPartnershipName())
                    .partners(etsOperatorDetails.getPartners())
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

    private static Set<OperationScope> resolveOperationScopes(String aviationActivity) {
        if ("Domestic UK and UK-EEA flights".equalsIgnoreCase(aviationActivity)) {
            return Set.of(OperationScope.UK_DOMESTIC, OperationScope.UK_TO_EEA_COUNTRIES);
        } else return Set.of(OperationScope.UK_DOMESTIC);
    }


    private static Map<String, List<EtsFileAttachment>> createEtsFileAttachments(List<EtsEmpOperatorDetails> etsEmpOperatorDetails) {
        return etsEmpOperatorDetails.stream().map(operatorDetail -> (operatorDetail.getAocStoredFileName()!=null && operatorDetail.getAocUploadedFileName()!=null) ?
                        EtsFileAttachment.builder()
                                .etsAccountId(operatorDetail.getEtsAccountId())
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
