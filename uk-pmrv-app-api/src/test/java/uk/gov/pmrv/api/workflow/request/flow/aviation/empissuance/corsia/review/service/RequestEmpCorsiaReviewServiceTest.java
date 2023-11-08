package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation.EmpIssuanceCorsiaReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@ExtendWith(MockitoExtension.class)
class RequestEmpCorsiaReviewServiceTest {
    @InjectMocks
    private RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Mock
    private EmpIssuanceCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;


    @Test
    void applySaveAction() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .methodAProcedures(EmpMethodAProcedures.builder().build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true),
                "methodAProcedures", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
                EmpCorsiaReviewGroup.METHOD_A_PROCEDURES, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
            )))
            .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
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
        EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        requestEmpCorsiaReviewService.applySaveAction(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(emissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
    }

    @Test
    void applySaveAction_with_reset_determination() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .methodAProcedures(EmpMethodAProcedures.builder().build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true),
                "methodAProcedures", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
                EmpCorsiaReviewGroup.METHOD_A_PROCEDURES, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
            )))
            .determination(EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build())
            .build();
        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
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
        EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        requestEmpCorsiaReviewService.applySaveAction(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(emissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
        assertThat(updatedRequestTaskPayload.getDetermination()).isNull();
    }

    @Test
    void saveReviewGroupDecision() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
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
        EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .reviewGroup(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
                .decision(EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build())
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        requestEmpCorsiaReviewService.saveReviewGroupDecision(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(requestTaskPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskPayload.getEmpSectionsCompleted());

        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        ));
    }

    @Test
    void saveReviewGroupDecision_reset_determination() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build())
            .empSectionsCompleted(Map.of(
                "additionalDocuments", List.of(true)
            ))
            .reviewGroupDecisions(new HashMap<>(Map.of(
                EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
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
        EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .reviewGroup(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
                .decision(EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build())
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED))
            .thenReturn(false);

        requestEmpCorsiaReviewService.saveReviewGroupDecision(requestTaskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(requestTaskPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(requestTaskPayload.getEmpSectionsCompleted());

        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(reviewSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(Map.of(
            EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build(),
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
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

        EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(determination)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        requestEmpCorsiaReviewService.saveDetermination(taskActionPayload, requestTask);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            updatedRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        assertEquals(determination, updatedRequestTaskPayload.getDetermination());
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(taskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReviewer = "peerReviewer";
        String reviewer = "reviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId(reviewer).build();
        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder().operatorName("name").build())
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
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.OPERATOR_DETAILS,
            EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).details(
                ReviewDecisionDetails.builder().notes("notes").build()).build()
        );
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN)
            .reason("withdrawn reason")
            .build();
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD)
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

        requestEmpCorsiaReviewService.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);

        EmpIssuanceCorsiaRequestPayload updatedRequestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

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
        EmpIssuanceCorsiaRequestPayload requestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .build();

        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
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
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED).details(ReviewDecisionDetails.builder().notes("notes").build()).build()
        );

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
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

        requestEmpCorsiaReviewService.saveRequestReturnForAmends(requestTask, pmrvUser);

        EmpIssuanceCorsiaRequestPayload updatedRequestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());

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
        EmissionsMonitoringPlanCorsia updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder()
                .exist(false)
                .build())
            .build();
        Map<String, List<Boolean>> updatedEmpSectionsCompleted = Map.of("task", List.of(false));
        Map<String, Boolean> updatedReviewSectionsCompleted = Map.of("group", false);

        EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload actionPayload =
            EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD)
                .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                .empSectionsCompleted(updatedEmpSectionsCompleted)
                .reviewSectionsCompleted(updatedReviewSectionsCompleted)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
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
        requestEmpCorsiaReviewService.saveAmend(actionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload updatedRequestTaskPayload =
            (EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(updatedRequestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(updatedEmissionsMonitoringPlan);
        assertThat(updatedRequestTaskPayload.getEmpSectionsCompleted()).isEqualTo(updatedEmpSectionsCompleted);
        assertThat(updatedRequestTaskPayload.getReviewSectionsCompleted()).isEqualTo(updatedReviewSectionsCompleted);
    }

    @Test
    void submitAmend() {
        String operator = "operator";
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId(operator).build();
        EmissionsMonitoringPlanCorsia monitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .build())
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        Map<UUID, String> empAttachments = Map.of(UUID.randomUUID(), "test.png");
        Map<String, Boolean> reviewSectionsCompleted = Map.of("group", true);
        Map<String, List<Boolean>> actionEmpSectionsCompleted = Map.of("task", List.of(true));

        EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload =
            EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .empSectionsCompleted(actionEmpSectionsCompleted)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .accountId(accountId)
                .payload(EmpIssuanceCorsiaRequestPayload.builder()
                    .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(
                            EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING).build())
                        .methodAProcedures(EmpMethodAProcedures.builder()
                            .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                                .procedureDescription("description")
                                .locationOfRecords("location")
                                .procedureDocumentName("document name")
                                .responsibleDepartmentOrRole("departament")
                                .procedureReference("reference")
                                .itSystemUsed("system")
                                .build())
                            .fuelDensity(EmpProcedureForm.builder()
                                .procedureDescription("description")
                                .locationOfRecords("location")
                                .procedureDocumentName("document name")
                                .responsibleDepartmentOrRole("departament")
                                .procedureReference("reference")
                                .itSystemUsed("system")
                                .build())
                            .build())
                        .build())
                    .reviewGroupDecisions(new HashMap<>(
                        Map.of(EmpCorsiaReviewGroup.METHOD_A_PROCEDURES, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).details(ReviewDecisionDetails.builder().notes("notes").build()).build())))
                    .build())
                .build())
            .payload(EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .emissionsMonitoringPlan(monitoringPlan)
                .empAttachments(empAttachments)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build())
            .build();

        EmissionsMonitoringPlanCorsiaContainer monitoringPlanContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .scheme(EmissionTradingScheme.CORSIA)
            .emissionsMonitoringPlan(monitoringPlan)
            .empAttachments(empAttachments)
            .build();

        String operatorName = "name";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .build();

        EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
            EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .operatorName(operatorName)
                        .build())
                    .abbreviations(EmpAbbreviations.builder().exist(false).build())
                    .build())
                .empSectionsCompleted(actionEmpSectionsCompleted)
                .empAttachments(empAttachments)
                .build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(aviationAccountInfo);

        // Invoke
        requestEmpCorsiaReviewService.submitAmend(actionPayload, requestTask, pmrvUser);

        // Verify
        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(EmpIssuanceCorsiaRequestPayload.class);

        EmpIssuanceCorsiaRequestPayload updatedRequestPayload =
            (EmpIssuanceCorsiaRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedRequestPayload.getEmissionsMonitoringPlan()).isEqualTo(monitoringPlan);
        assertThat(updatedRequestPayload.getEmpAttachments()).isEqualTo(empAttachments);
        assertThat(updatedRequestPayload.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).isEqualTo(actionEmpSectionsCompleted);
        assertThat(updatedRequestPayload.getReviewGroupDecisions()).isEmpty();

        verify(empCorsiaValidatorService, times(1))
            .validateEmissionsMonitoringPlan(monitoringPlanContainer);
        verify(requestAviationAccountQueryService, times(1))
            .getAccountInfo(accountId);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), amendsSubmittedRequestActionPayload,
                RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED, operator);
    }
}