package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;

class EmpVariationUkEtsSubmitMapperTest {

	private final EmpVariationUkEtsSubmitMapper mapper = Mappers.getMapper(EmpVariationUkEtsSubmitMapper.class);

    @Test
    void toEmpVariationUkEtsApplicationSubmittedRequestActionPayload() {
    	UUID att1 = UUID.randomUUID();
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
				.abbreviations(EmpAbbreviations.builder().exist(true).build())
				.operatorDetails(EmpOperatorDetails.builder()
						.operatorName("opName1")
						.crcoCode("crcoCode1")
						.build())
				.build();
    	EmpVariationUkEtsApplicationSubmitRequestTaskPayload requestPayload = EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
    			.emissionsMonitoringPlan(emp)
    			.empVariationDetails(EmpVariationUkEtsDetails.builder()
    					.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
    					.reason("reason1")
    					.build())
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
    			.crcoCode("crcoCode2")
    			.build();
    	
    	EmpVariationUkEtsApplicationSubmittedRequestActionPayload result = mapper.toEmpVariationUkEtsApplicationSubmittedRequestActionPayload(requestPayload, requestAviationAccountInfo);
    	
    	assertThat(result).isEqualTo(EmpVariationUkEtsApplicationSubmittedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    					.abbreviations(EmpAbbreviations.builder().exist(true).build())
    					.operatorDetails(EmpOperatorDetails.builder()
    							.operatorName("opName1")
    							.crcoCode("crcoCode2")
    							.build())
    					.build())
    			.empVariationDetails(EmpVariationUkEtsDetails.builder()
    					.changes(List.of(EmpVariationUkEtsChangeType.DIFFERENT_FUMMS))
    					.reason("reason1")
    					.build())
    			.serviceContactDetails(requestAviationAccountInfo.getServiceContactDetails())
    			.empSectionsCompleted(Map.of("section1", List.of(Boolean.TRUE)))
    			.empAttachments(Map.of(att1, "att1.pdf"))
    			.build());
    }
}
