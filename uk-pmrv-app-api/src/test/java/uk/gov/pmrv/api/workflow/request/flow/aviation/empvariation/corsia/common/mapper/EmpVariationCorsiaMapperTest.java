package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;

class EmpVariationCorsiaMapperTest {

	private final EmpVariationCorsiaMapper mapper = Mappers.getMapper(EmpVariationCorsiaMapper.class);

    @Test
    void toEmissionsMonitoringPlanCorsiaContainer_from_requestTaskPayload() {
    	EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescriptionCorsia.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
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
    	
    	EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload = EmpVariationCorsiaApplicationSubmitRequestTaskPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.serviceContactDetails(scd)
    			.build();
    	
    	
    	EmissionsMonitoringPlanCorsiaContainer result = mapper.toEmissionsMonitoringPlanCorsiaContainer(taskPayload);
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanCorsiaContainer.builder()
    			.scheme(EmissionTradingScheme.CORSIA)
    			.serviceContactDetails(scd)
    			.emissionsMonitoringPlan(emp)
    		.build());
    }
    
    @Test
    void toEmissionsMonitoringPlanCorsiaContainer_from_requestPayload() {
    	EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescriptionCorsia.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
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
    	
    	EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.build();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	RequestAviationAccountInfo requestAviationAccountInfo = RequestAviationAccountInfo.builder()
    			.serviceContactDetails(scd)
    			.build();
    	
    	EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
    	
    	EmissionsMonitoringPlanCorsiaContainer result = 
    			mapper.toEmissionsMonitoringPlanCorsiaContainer(requestPayload, requestAviationAccountInfo, scheme);
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanCorsiaContainer.builder()
    			.scheme(scheme)
    			.serviceContactDetails(scd)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescriptionCorsia.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
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
    void cloneEmissionsMonitoringPlanCorsia() {
    	EmissionsMonitoringPlanCorsia source = EmissionsMonitoringPlanCorsia.builder()
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("name1")
    					.activitiesDescription(ActivitiesDescriptionCorsia.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
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
    	
    	EmissionsMonitoringPlanCorsia result = mapper.cloneEmissionsMonitoringPlanCorsia(source, "name2");
    	
    	assertThat(result).isEqualTo(EmissionsMonitoringPlanCorsia.builder()
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("name2")
    					.activitiesDescription(ActivitiesDescriptionCorsia.builder()
    							.activityDescription("acDescr")
    							.build())
    					.airOperatingCertificate(AirOperatingCertificateCorsia.builder()
    							.certificateExist(true)
    							.build())
    					.flightIdentification(FlightIdentification.builder()
    							.flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
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
}
