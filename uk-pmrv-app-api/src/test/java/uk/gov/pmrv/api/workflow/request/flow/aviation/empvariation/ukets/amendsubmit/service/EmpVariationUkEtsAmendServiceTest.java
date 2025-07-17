package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsAmendServiceTest {

	@InjectMocks
    private EmpVariationUkEtsAmendService service;
		
	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
	
	@Mock
    private RequestService requestService;
	
	@Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	@Test
    void saveAmend() {
        EmissionsMonitoringPlanUkEts updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder()
                        .exist(false)
                        .build())
                .build();
        Map<String, List<Boolean>> updatedEmpSectionsCompleted = Map.of("task", List.of(false));
        Map<String, Boolean> updatedReviewSectionsCompleted = Map.of("group", false);

        EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload actionPayload =
        		EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_VARIATION_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD)
                        .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                        .empSectionsCompleted(updatedEmpSectionsCompleted)
                        .reviewSectionsCompleted(updatedReviewSectionsCompleted)
                        .empVariationDetails(EmpVariationUkEtsDetails.builder().reason("reason").build())
                        .empVariationDetailsCompleted(Boolean.TRUE)
                        .empVariationDetailsReviewCompleted(Boolean.TRUE)
                        .empVariationDetailsAmendCompleted(Boolean.TRUE)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .abbreviations(EmpAbbreviations.builder()
                                        .exist(true)
                                        .abbreviationDefinitions(List.of(
                                                EmpAbbreviationDefinition.builder().definition("definition").build())
                                        )
                                        .build())
                                .build())
                        .empSectionsCompleted(Map.of("task", List.of(true)))
                        .reviewSectionsCompleted(Map.of("group", true))
                        .build())
                .build();

        // Invoke
        service.saveAmend(actionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.class);

        EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
                (EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(updatedEmissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).isEqualTo(updatedEmpSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).isEqualTo(updatedReviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getEmpVariationDetails()).isEqualTo(updatedRequestTaskPayload.getEmpVariationDetails());
        assertThat(updatedRequestTaskPayload.getEmpVariationDetailsCompleted()).isEqualTo(updatedRequestTaskPayload.getEmpVariationDetailsCompleted());
        assertThat(updatedRequestTaskPayload.getEmpVariationDetailsReviewCompleted()).isEqualTo(updatedRequestTaskPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestTaskPayload.getEmpVariationDetailsAmendCompleted()).isEqualTo(updatedRequestTaskPayload.getEmpVariationDetailsAmendCompleted());
    }
    
    @Test
    void submitAmend() {
        String operator = "operator";
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId(operator).build();
        EmissionsMonitoringPlanUkEts monitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                        .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .build();
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "test.png");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("group", true);
        Map<String, List<Boolean>> actionEmpSectionsCompleted = Map.of("task", List.of(true));

        EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload actionPayload =
        		EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_VARIATION_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                        .empSectionsCompleted(actionEmpSectionsCompleted)
                        .build();
        
        EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload = EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
		        .emissionsMonitoringPlan(monitoringPlan)
		        .empAttachments(empAttachments)
		        .reviewSectionsCompleted(reviewSectionsCompleted)
		        .empVariationDetails(EmpVariationUkEtsDetails.builder().reason("reason").build())
		        .empVariationDetailsCompleted(Boolean.TRUE)
		        .empVariationDetailsReviewCompleted(Boolean.TRUE)
		        .empVariationDetailsAmendCompleted(Boolean.TRUE)
		        .build();
        EmpVariationUkEtsRequestPayload requestPayload = EmpVariationUkEtsRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING).build())
                .dataGaps(EmpDataGaps.builder().dataGaps("dataGaps").build())
                .build())
            .reviewGroupDecisions(new HashMap<>(
                Map.of(EmpUkEtsReviewGroup.DATA_GAPS, EmpVariationReviewDecision.builder()
                    .type(EmpVariationReviewDecisionType.ACCEPTED)
                    .details(EmpAcceptedVariationDecisionDetails.builder()
                        .notes("notes")
                        .build())
                    .build())))
            .build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(accountId)
                        .payload(requestPayload)
                        .build())
                .payload(taskPayload)
                .build();

        EmissionsMonitoringPlanUkEtsContainer monitoringPlanContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(monitoringPlan)
                .empAttachments(empAttachments)
                .build();
        
        String operatorName = "name";
        String crcoCode = "code";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
                .operatorName(operatorName)
                .crcoCode(crcoCode)
                .build();

        EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
        		EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .operatorDetails(EmpOperatorDetails.builder()
                                        .operatorName(operatorName)
                                        .crcoCode(crcoCode)
                                        .build())
                                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                                .build())
                        .empVariationDetails(EmpVariationUkEtsDetails.builder().reason("reason").build())
                        .empSectionsCompleted(actionEmpSectionsCompleted)
                        .empAttachments(empAttachments)
                        .build();
        
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        // Invoke
        service.submitAmend(actionPayload, requestTask, appUser);

        // Verify
        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(EmpVariationUkEtsRequestPayload.class);

        EmpVariationUkEtsRequestPayload updatedRequestPayload =
                (EmpVariationUkEtsRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedRequestPayload.getEmissionsMonitoringPlan()).isEqualTo(monitoringPlan);
        assertThat(updatedRequestPayload.getEmpAttachments()).isEqualTo(empAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).isEqualTo(actionEmpSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpVariationDetails()).isEqualTo(taskPayload.getEmpVariationDetails());
        assertThat(updatedRequestPayload.getEmpVariationDetailsCompleted()).isEqualTo(taskPayload.getEmpVariationDetailsCompleted());
        assertThat(updatedRequestPayload.getEmpVariationDetailsReviewCompleted()).isEqualTo(taskPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).isEmpty();


        verify(empUkEtsValidatorService, times(1))
                .validateEmissionsMonitoringPlan(monitoringPlanContainer);
        verify(requestService, times(1))
                .addActionToRequest(requestTask.getRequest(), amendsSubmittedRequestActionPayload,
                        RequestActionType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED, operator);
    }
}
