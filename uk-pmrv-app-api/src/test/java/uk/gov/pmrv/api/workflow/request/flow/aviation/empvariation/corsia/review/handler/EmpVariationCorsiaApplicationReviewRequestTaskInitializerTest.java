package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaApplicationReviewRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationCorsiaApplicationReviewRequestTaskInitializer handler;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void initializePayload() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;
		
		EmissionsMonitoringPlanCorsiaContainer originalEmpContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
						.builder()
						.abbreviations(EmpAbbreviations
								.builder()
								.exist(false)
								.build())
						.build())
				.empAttachments(Map.of(attachment, "attachment"))
				.build();
		EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia
				.builder()
				.abbreviations(EmpAbbreviations
						.builder()
						.exist(false)
						.build())
				.additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
				.operatorDetails(EmpCorsiaOperatorDetails.builder().operatorName("name").build())
				.build();
		EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(attachment, "attachment"))
				.build();
		EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
				.emissionsMonitoringPlan(empContainer.getEmissionsMonitoringPlan())
				.empVariationDetails(EmpVariationCorsiaDetails.builder().reason("test reason").build())
				.empVariationDetailsCompleted(Boolean.TRUE)
				.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
				.build();
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanCorsiaDTO dto = EmissionsMonitoringPlanCorsiaDTO
				.builder()
				.empContainer(originalEmpContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.operatorName("name")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(dto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		// invoke
		RequestTaskPayload result = handler.initializePayload(request);
		EmpVariationCorsiaApplicationReviewRequestTaskPayload variationTaskPayloadResult = 
				(EmpVariationCorsiaApplicationReviewRequestTaskPayload) result;
		
		assertThat(variationTaskPayloadResult.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName())
			.isEqualTo(requestPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName());
		assertThat(variationTaskPayloadResult.getEmissionsMonitoringPlan().getAbbreviations())
			.isEqualTo(requestPayload.getEmissionsMonitoringPlan().getAbbreviations());
		assertThat(variationTaskPayloadResult.getEmpVariationDetails()).isEqualTo(requestPayload.getEmpVariationDetails());
		assertThat(variationTaskPayloadResult.getEmpVariationDetailsCompleted()).isEqualTo(requestPayload.getEmpVariationDetailsCompleted());
		assertThat(variationTaskPayloadResult.getEmpSectionsCompleted()).isEqualTo(requestPayload.getEmpSectionsCompleted());
		assertThat(variationTaskPayloadResult.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
		assertThat(variationTaskPayloadResult.getOriginalEmpContainer()).isEqualTo(originalEmpContainer);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions().keySet())
			.containsExactlyInAnyOrder(EmpCorsiaReviewGroup.SERVICE_CONTACT_DETAILS,
					EmpCorsiaReviewGroup.OPERATOR_DETAILS,
					EmpCorsiaReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
					EmpCorsiaReviewGroup.MONITORING_APPROACH, 
					EmpCorsiaReviewGroup.EMISSION_SOURCES,
					EmpCorsiaReviewGroup.MANAGEMENT_PROCEDURES,
					EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
					EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS,
					EmpCorsiaReviewGroup.DATA_GAPS);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
	}
}
