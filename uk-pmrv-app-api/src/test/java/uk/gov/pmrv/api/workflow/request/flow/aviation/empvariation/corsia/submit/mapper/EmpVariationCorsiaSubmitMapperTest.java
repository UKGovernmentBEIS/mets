package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;

class EmpVariationCorsiaSubmitMapperTest {

	private final EmpVariationCorsiaSubmitMapper mapper = Mappers.getMapper(EmpVariationCorsiaSubmitMapper.class);

    @Test
    void toEmpVariationCorsiaApplicationSubmittedRequestActionPayload() {
    	UUID att1 = UUID.randomUUID();
    	EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder()
						.operatorName("opName1")
						.build())
				.build();
    	EmpVariationCorsiaApplicationSubmitRequestTaskPayload requestPayload = 
    			EmpVariationCorsiaApplicationSubmitRequestTaskPayload.builder()
	    			.emissionsMonitoringPlan(emp)
	    			.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
	    			.empAttachments(Map.of(att1, "att1.pdf"))
	    			.build();
    	
    	ServiceContactDetails scd = ServiceContactDetails.builder()
    			.email("email")
    			.name("name")
    			.roleCode("roleCode")
    			.build();
    	
    	RequestAviationAccountInfo requestAviationAccountInfo = RequestAviationAccountInfo.builder()
    			.serviceContactDetails(scd)
    			.operatorName("opName2")
    			.build();
    	
    	EmpVariationCorsiaApplicationSubmittedRequestActionPayload result = mapper.toEmpVariationCorsiaApplicationSubmittedRequestActionPayload(requestPayload, requestAviationAccountInfo);
    	
    	assertThat(result).isEqualTo(EmpVariationCorsiaApplicationSubmittedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED_PAYLOAD)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
    					.abbreviations(EmpAbbreviations.builder().exist(true).build())
    					.operatorDetails(EmpCorsiaOperatorDetails.builder()
    							.operatorName("opName1")
    							.build())
    					.build())
    			.serviceContactDetails(requestAviationAccountInfo.getServiceContactDetails())
    			.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
    			.empAttachments(Map.of(att1, "att1.pdf"))
    			.build());
    }
}
