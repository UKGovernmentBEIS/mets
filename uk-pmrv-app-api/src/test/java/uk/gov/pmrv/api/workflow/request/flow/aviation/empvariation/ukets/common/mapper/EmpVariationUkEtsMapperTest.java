package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;

class EmpVariationUkEtsMapperTest {

	private final EmpVariationUkEtsMapper mapper = Mappers.getMapper(EmpVariationUkEtsMapper.class);

    @Test
    void toEmissionsMonitoringPlanUkEtsContainer_from_requestTaskPayload() {
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
    			.operatorDetails(EmpOperatorDetails.builder()
    					.crcoCode("crco1")
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescription.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificate.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
    							.build())
    					.operatingLicense(OperatingLicense.builder()
    							.issuingAuthority("issAuth")
    							.build())
    					.organisationStructure(IndividualOrganisation.builder()
    							.legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
    							.build())
    					.build())
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(true)
    					.build())
    			.additionalDocuments(EmpAdditionalDocuments.builder()
    					.exist(true)
    					.build())
    			.build();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	
    	EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.serviceContactDetails(scd)
    			.build();
    	
    	
    	EmissionsMonitoringPlanUkEtsContainer result = mapper.toEmissionsMonitoringPlanUkEtsContainer(taskPayload);
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanUkEtsContainer.builder()
    			.scheme(EmissionTradingScheme.UK_ETS_AVIATION)
    			.serviceContactDetails(scd)
    			.emissionsMonitoringPlan(emp)
    		.build());
    }
    
    @Test
    void toEmissionsMonitoringPlanUkEtsContainer_from_requestPayload() {
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
    			.operatorDetails(EmpOperatorDetails.builder()
    					.crcoCode("crco1")
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescription.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificate.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
    							.build())
    					.operatingLicense(OperatingLicense.builder()
    							.issuingAuthority("issAuth")
    							.build())
    					.organisationStructure(IndividualOrganisation.builder()
    							.legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
    							.build())
    					.build())
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(true)
    					.build())
    			.additionalDocuments(EmpAdditionalDocuments.builder()
    					.exist(true)
    					.build())
    			.build();
    	
    	EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.build();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	RequestAviationAccountInfo requestAviationAccountInfo = RequestAviationAccountInfo.builder()
    			.serviceContactDetails(scd)
    			.crcoCode("crco2")
    			.build();
    	
    	EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
    	
    	EmissionsMonitoringPlanUkEtsContainer result = mapper.toEmissionsMonitoringPlanUkEtsContainer(requestPayload, requestAviationAccountInfo, scheme);
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanUkEtsContainer.builder()
    			.scheme(scheme)
    			.serviceContactDetails(scd)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    			.operatorDetails(EmpOperatorDetails.builder()
    					.crcoCode("crco2")
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescription.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificate.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
    							.build())
    					.operatingLicense(OperatingLicense.builder()
    							.issuingAuthority("issAuth")
    							.build())
    					.organisationStructure(IndividualOrganisation.builder()
    							.legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
    							.build())
    					.build())
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(true)
    					.build())
    			.additionalDocuments(EmpAdditionalDocuments.builder()
    					.exist(true)
    					.build())
    			.build())
    		.build());
    }
    
    @Test
    void cloneEmissionsMonitoringPlanUkEts() {
    	EmissionsMonitoringPlanUkEts source = EmissionsMonitoringPlanUkEts.builder()
    			.operatorDetails(EmpOperatorDetails.builder()
    					.crcoCode("crco1")
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescription.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificate.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
    							.build())
    					.operatingLicense(OperatingLicense.builder()
    							.issuingAuthority("issAuth")
    							.build())
    					.organisationStructure(IndividualOrganisation.builder()
    							.legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
    							.build())
    					.build())
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(true)
    					.build())
    			.additionalDocuments(EmpAdditionalDocuments.builder()
    					.exist(true)
    					.build())
    			.build();
    	
    	EmissionsMonitoringPlanUkEts result = mapper.cloneEmissionsMonitoringPlanUkEts(source, "name2", "crco2");
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanUkEts.builder()
    			.operatorDetails(EmpOperatorDetails.builder()
    					.crcoCode("crco2")
    					.operatorName("name2")
    					.activitiesDescription(ActivitiesDescription.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificate.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
    							.build())
    					.operatingLicense(OperatingLicense.builder()
    							.issuingAuthority("issAuth")
    							.build())
    					.organisationStructure(IndividualOrganisation.builder()
    							.legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
    							.build())
    					.build())
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(true)
    					.build())
    			.additionalDocuments(EmpAdditionalDocuments.builder()
    					.exist(true)
    					.build())
    			.build());
    }
    

    @Test
    void toEmpVariationRequestInfo() {
    	LocalDateTime endDate = LocalDateTime.now();
    	LocalDateTime submissionDate = LocalDateTime.now();
    	EmpVariationRequestMetadata metadata = EmpVariationRequestMetadata.builder()
				.type(RequestMetadataType.EMP_VARIATION)
				.empConsolidationNumber(2)
				.build();
    	Request request = Request.builder()
    			.type(RequestType.EMP_VARIATION_UKETS)
    			.endDate(endDate)
    			.submissionDate(submissionDate)
    			.id("id")
    			.metadata(metadata)
    			.build();
    	
    	EmpVariationRequestInfo result = mapper.toEmpVariationRequestInfo(request);
    	
    	assertThat(result).isEqualTo(EmpVariationRequestInfo.builder()
    			.endDate(endDate)
    			.submissionDate(submissionDate)
    			.id("id")
    			.metadata(metadata)
    			.build());
    }
    
}
