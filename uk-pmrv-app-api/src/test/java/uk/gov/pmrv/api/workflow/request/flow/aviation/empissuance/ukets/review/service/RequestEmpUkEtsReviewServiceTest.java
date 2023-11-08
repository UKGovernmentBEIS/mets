package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestEmpUkEtsReviewServiceTest {

    @InjectMocks
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Mock
    private EmpIssuanceUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void applySaveAction() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .methodAProcedures(EmpMethodAProcedures.builder().build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true),
                "methodAProcedures", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
                EmpUkEtsReviewGroup.METHOD_A_PROCEDURES, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
            )))
            .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .additionalDocuments(EmpAdditionalDocuments.builder().exist(true).documents(Set.of(UUID.randomUUID())).build())
            .build();
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "additionalDocuments", List.of(true)
        );

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true,
            "additionalDocuments", true
        );
        EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        requestEmpUkEtsReviewService.applySaveAction(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(emissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
    }

    @Test
    void applySaveAction_with_reset_determination() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                        .methodAProcedures(EmpMethodAProcedures.builder().build())
                        .build())
                .empSectionsCompleted(Map.of(
                        "additionalDocuments", List.of(true),
                        "methodAProcedures", List.of(true)
                ))
                .reviewGroupDecisions(new HashMap<>(Map.of(
                        EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
                        EmpUkEtsReviewGroup.METHOD_A_PROCEDURES, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
                )))
                .determination(EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build())
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(true).documents(Set.of(UUID.randomUUID())).build())
                .build();
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
                "abbreviations", List.of(true),
                "additionalDocuments", List.of(true)
        );

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
                "abbreviations", true,
                "additionalDocuments", true
        );
        EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
                EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload.builder()
                        .emissionsMonitoringPlan(emissionsMonitoringPlan)
                        .empSectionsCompleted(empSectionsCompleted)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        requestEmpUkEtsReviewService.applySaveAction(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
                updatedRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(emissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
                EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
        assertThat(updatedRequestTaskPayload.getDetermination()).isNull();
    }

    @Test
    void saveReviewGroupDecision() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
            )))
            .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true,
            "additionalDocuments", true
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "additionalDocuments", List.of(true)
        );
        EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .reviewGroup(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
                .decision(EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build())
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        requestEmpUkEtsReviewService.saveReviewGroupDecision(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(requestTaskPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskPayload.getEmpSectionsCompleted());

        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
        verify(reviewDeterminationValidatorService, never()).isValid(any(), any());
    }

    @Test
    void saveReviewGroupDecision_reset_determination() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                        .build())
                .empSectionsCompleted(Map.of(
                        "additionalDocuments", List.of(true)
                ))
                .reviewGroupDecisions(new HashMap<>(Map.of(
                        EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
                )))
                .determination(EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build())
                .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
                "abbreviations", true,
                "additionalDocuments", true
        );

        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
                "abbreviations", List.of(true),
                "additionalDocuments", List.of(true)
        );
        EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
                EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                        .reviewGroup(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
                        .decision(EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build())
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .empSectionsCompleted(empSectionsCompleted)
                        .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED))
                .thenReturn(false);

        requestEmpUkEtsReviewService.saveReviewGroupDecision(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
                updatedRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(requestTaskPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskPayload.getEmpSectionsCompleted());

        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
                EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
                EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
        assertThat(updatedRequestTaskPayload.getDetermination()).isNull();
        verify(reviewDeterminationValidatorService, times(1))
                .isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED);
    }

    @Test
    void saveDetermination() {
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("reason")
            .build();

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true,
            "additionalDocuments", true
        );

        EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(determination)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        requestEmpUkEtsReviewService.saveDetermination(taskActionPayload, requestTask);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertEquals(determination, updatedRequestTaskPayload.getDetermination());
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void saveDecisionNotification() {
        String reviewer = "regUser";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operUser")).signatory(reviewer).build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder().operatorName("name").crcoCode("crcoCode").build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "operatorDetails", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "operatorDetails", true
            );
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS,
            EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .determination(determination)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        //invoke
        requestEmpUkEtsReviewService.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);

        EmpIssuanceUkEtsRequestPayload updatedRequestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(determination, updatedRequestPayload.getDetermination());
        assertEquals(decisionNotification, updatedRequestPayload.getDecisionNotification());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReviewer = "peerReviewer";
        String reviewer = "reviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder().operatorName("name").crcoCode("crcoCode").build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "operatorDetails", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "operatorDetails", true
        );
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS,
            EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN)
            .reason("withdrawn reason")
            .build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .determination(determination)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        requestEmpUkEtsReviewService.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);

        EmpIssuanceUkEtsRequestPayload updatedRequestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(determination, updatedRequestPayload.getDetermination());
        assertEquals(selectedPeerReviewer, updatedRequestPayload.getRegulatorPeerReviewer());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }
    
    @Test
    void saveRequestReturnForAmends() {
        String reviewer = "reviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true)
        );
        Map<UUID, String> reviewAttachments = Map.of(
            UUID.randomUUID(), "reviewAttachment"
        );
        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "abbreviations", true
        );
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED).details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empAttachments(empAttachments)
            .empSectionsCompleted(empSectionsCompleted)
            .reviewAttachments(reviewAttachments)
            .reviewSectionsCompleted(reviewSectionsCompleted)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .request(request)
            .build();

        requestEmpUkEtsReviewService.saveRequestReturnForAmends(requestTask, pmrvUser);

        EmpIssuanceUkEtsRequestPayload updatedRequestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertEquals(reviewer, updatedRequestPayload.getRegulatorReviewer());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(updatedRequestPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
    }

    @Test
    void saveAmend() {
        EmissionsMonitoringPlanUkEts updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder()
                        .exist(false)
                        .build())
                .build();
        Map<String, List<Boolean>> updatedEmpSectionsCompleted = Map.of("task", List.of(false));
        Map<String, Boolean> updatedReviewSectionsCompleted = Map.of("group", false);

        EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload actionPayload =
                EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD)
                        .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                        .empSectionsCompleted(updatedEmpSectionsCompleted)
                        .reviewSectionsCompleted(updatedReviewSectionsCompleted)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
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
        requestEmpUkEtsReviewService.saveAmend(actionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
                (EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(updatedEmissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).isEqualTo(updatedEmpSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).isEqualTo(updatedReviewSectionsCompleted);
    }

    @Test
    void submitAmend() {
        String operator = "operator";
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId(operator).build();
        EmissionsMonitoringPlanUkEts monitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                        .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .build();
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "test.png");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("group", true);
        Map<String, List<Boolean>> actionEmpSectionsCompleted = Map.of("task", List.of(true));

        EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload actionPayload =
                EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                        .empSectionsCompleted(actionEmpSectionsCompleted)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .accountId(accountId)
                        .payload(EmpIssuanceUkEtsRequestPayload.builder()
                            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING).build())
                                .dataGaps(EmpDataGaps.builder().dataGaps("dataGaps").build())
                                .build())
                            .reviewGroupDecisions(new HashMap<>(
                                Map.of(EmpUkEtsReviewGroup.DATA_GAPS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).details(ReviewDecisionDetails.builder().notes("notes").build()).build())))
                            .build())
                        .build())
                .payload(EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(monitoringPlan)
                        .empAttachments(empAttachments)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build())
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

        EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
                EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .operatorDetails(EmpOperatorDetails.builder()
                                        .operatorName(operatorName)
                                        .crcoCode(crcoCode)
                                        .build())
                                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                                .build())
                        .empSectionsCompleted(actionEmpSectionsCompleted)
                        .empAttachments(empAttachments)
                        .build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        // Invoke
        requestEmpUkEtsReviewService.submitAmend(actionPayload, requestTask, pmrvUser);

        // Verify
        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(EmpIssuanceUkEtsRequestPayload.class);

        EmpIssuanceUkEtsRequestPayload updatedRequestPayload =
                (EmpIssuanceUkEtsRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedRequestPayload.getEmissionsMonitoringPlan()).isEqualTo(monitoringPlan);
        assertThat(updatedRequestPayload.getEmpAttachments()).isEqualTo(empAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).isEqualTo(actionEmpSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).isEmpty();

        verify(empUkEtsValidatorService, times(1))
                .validateEmissionsMonitoringPlan(monitoringPlanContainer);
        verify(requestAviationAccountQueryService, times(1))
                .getAccountInfo(accountId);
        verify(requestService, times(1))
                .addActionToRequest(requestTask.getRequest(), amendsSubmittedRequestActionPayload,
                        RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED, operator);
    }
}