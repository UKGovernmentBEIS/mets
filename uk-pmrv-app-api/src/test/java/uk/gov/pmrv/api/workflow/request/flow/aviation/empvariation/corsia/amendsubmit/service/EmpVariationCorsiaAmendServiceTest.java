package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaAmendServiceTest {

    @InjectMocks
    private EmpVariationCorsiaAmendService service;

    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void saveAmend() {
        EmissionsMonitoringPlanCorsia updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder()
                .exist(false)
                .build())
            .build();
        Map<String, List<Boolean>> updatedEmpSectionsCompleted = Map.of("task", List.of(false));
        Map<String, Boolean> updatedReviewSectionsCompleted = Map.of("group", false);

        EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload actionPayload =
            EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD)
                .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                .empSectionsCompleted(updatedEmpSectionsCompleted)
                .reviewSectionsCompleted(updatedReviewSectionsCompleted)
                .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("reason").build())
                .empVariationDetailsCompleted(Boolean.TRUE)
                .empVariationDetailsReviewCompleted(Boolean.TRUE)
                .empVariationDetailsAmendCompleted(Boolean.TRUE)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
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
        assertThat(requestTask.getPayload()).isInstanceOf(
            EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.class);

        EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
            (EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(updatedEmissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).isEqualTo(updatedEmpSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).isEqualTo(updatedReviewSectionsCompleted);
        assertThat(actionPayload.getEmpVariationDetails()).isEqualTo(
            updatedRequestTaskPayload.getEmpVariationDetails());
        assertThat(actionPayload.getEmpVariationDetailsCompleted()).isEqualTo(
            updatedRequestTaskPayload.getEmpVariationDetailsCompleted());
        assertThat(actionPayload.getEmpVariationDetailsReviewCompleted()).isEqualTo(
            updatedRequestTaskPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(actionPayload.getEmpVariationDetailsAmendCompleted()).isEqualTo(
            updatedRequestTaskPayload.getEmpVariationDetailsAmendCompleted());
    }

    @Test
    void submitAmend() {
        String operator = "operator";
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId(operator).build();
        EmissionsMonitoringPlanCorsia monitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .build())
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "test.png");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("group", true);
        Map<String, List<Boolean>> actionEmpSectionsCompleted = Map.of("task", List.of(true));

        EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload =
            EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .empSectionsCompleted(actionEmpSectionsCompleted)
                .build();

        EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
            EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(monitoringPlan)
                .empAttachments(empAttachments)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("reason").build())
                .empVariationDetailsCompleted(Boolean.TRUE)
                .empVariationDetailsReviewCompleted(Boolean.TRUE)
                .empVariationDetailsAmendCompleted(Boolean.TRUE)
                .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder().monitoringApproachType(
                    EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING).build())
                .methodAProcedures(EmpMethodAProcedures.builder()
                    .fuelDensity(EmpProcedureForm.builder()
                        .procedureReference("procedure Reference")
                        .responsibleDepartmentOrRole("departament")
                        .procedureDocumentName("document name")
                        .locationOfRecords("location")
                        .procedureDescription("description")
                        .itSystemUsed("system")
                        .build())
                    .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                        .procedureReference("procedure Reference")
                        .responsibleDepartmentOrRole("departament")
                        .procedureDocumentName("document name")
                        .locationOfRecords("location")
                        .procedureDescription("description")
                        .itSystemUsed("system")
                        .build())
                    .build())
                .build())
            .reviewGroupDecisions(new HashMap<>(
                Map.of(EmpCorsiaReviewGroup.METHOD_A_PROCEDURES, EmpVariationReviewDecision.builder()
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

        EmissionsMonitoringPlanCorsiaContainer monitoringPlanContainer =
            EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(monitoringPlan)
                .empAttachments(empAttachments)
                .build();

        String operatorName = "name";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .build();

        EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
            EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .operatorName(operatorName)
                        .build())
                    .abbreviations(EmpAbbreviations.builder().exist(false).build())
                    .build())
                .empVariationDetails(EmpVariationCorsiaDetails.builder().reason("reason").build())
                .empSectionsCompleted(actionEmpSectionsCompleted)
                .empAttachments(empAttachments)
                .build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        // Invoke
        service.submitAmend(actionPayload, requestTask, appUser);

        // Verify
        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(EmpVariationCorsiaRequestPayload.class);

        EmpVariationCorsiaRequestPayload updatedRequestPayload =
            (EmpVariationCorsiaRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedRequestPayload.getEmissionsMonitoringPlan()).isEqualTo(monitoringPlan);
        assertThat(updatedRequestPayload.getEmpAttachments()).isEqualTo(empAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).isEqualTo(actionEmpSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpVariationDetails()).isEqualTo(taskPayload.getEmpVariationDetails());
        assertThat(updatedRequestPayload.getEmpVariationDetailsCompleted()).isEqualTo(
            taskPayload.getEmpVariationDetailsCompleted());
        assertThat(updatedRequestPayload.getEmpVariationDetailsReviewCompleted()).isEqualTo(
            taskPayload.getEmpVariationDetailsReviewCompleted());
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).isEmpty();


        verify(empCorsiaValidatorService, times(1))
            .validateEmissionsMonitoringPlan(monitoringPlanContainer);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), amendsSubmittedRequestActionPayload,
                RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED, operator);
    }
}
