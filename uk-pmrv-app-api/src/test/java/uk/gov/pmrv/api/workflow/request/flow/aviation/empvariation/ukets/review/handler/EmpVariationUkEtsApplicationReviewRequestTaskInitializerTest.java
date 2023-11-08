package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApplicationReviewRequestTaskInitializerTest {

	@InjectMocks
    private EmpVariationUkEtsApplicationReviewRequestTaskInitializer handler;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
	void initializePayload() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;
		
		EmissionsMonitoringPlanUkEtsContainer originalEmpContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
						.builder()
						.abbreviations(EmpAbbreviations
								.builder()
								.exist(false)
								.build())
						.build())
				.empAttachments(Map.of(attachment, "attachment"))
				.build();
		EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts
				.builder()
				.abbreviations(EmpAbbreviations
						.builder()
						.exist(false)
						.build())
				.additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
				.operatorDetails(EmpOperatorDetails.builder().operatorName("name").build())
				.build();
		EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
				.emissionsMonitoringPlan(emp)
				.empAttachments(Map.of(attachment, "attachment"))
				.build();
		EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
				.emissionsMonitoringPlan(empContainer.getEmissionsMonitoringPlan())
				.empVariationDetails(EmpVariationUkEtsDetails.builder().reason("test reason").build())
				.empVariationDetailsCompleted(Boolean.TRUE)
				.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
				.build();
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		EmissionsMonitoringPlanUkEtsDTO dto = EmissionsMonitoringPlanUkEtsDTO
				.builder()
				.empContainer(originalEmpContainer)
				.build();
		
		RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo
				.builder()
				.crcoCode("crcoCode")
				.operatorName("name")
				.serviceContactDetails(ServiceContactDetails.builder().name("name2").build())
				.build();
		
		when(empQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(dto));
		when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
		
		// invoke
		RequestTaskPayload result = handler.initializePayload(request);
		EmpVariationUkEtsApplicationReviewRequestTaskPayload variationTaskPayloadResult = (EmpVariationUkEtsApplicationReviewRequestTaskPayload) result;
		
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
			.containsExactlyInAnyOrder(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS,
					EmpUkEtsReviewGroup.OPERATOR_DETAILS,
					EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
					EmpUkEtsReviewGroup.MONITORING_APPROACH, 
					EmpUkEtsReviewGroup.EMISSION_SOURCES,
					EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES,
					EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, 
					EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
					EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS,
					EmpUkEtsReviewGroup.LATE_SUBMISSION);
		
		verify(empQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
		verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
	}
}
