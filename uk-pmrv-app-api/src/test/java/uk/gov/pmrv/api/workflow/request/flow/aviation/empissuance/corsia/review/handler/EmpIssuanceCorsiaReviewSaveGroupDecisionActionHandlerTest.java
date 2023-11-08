package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewSaveGroupDecisionActionHandler reviewSaveGroupDecisionActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewGroup(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
                .decision(EmpIssuanceReviewDecision.builder()
                    .type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(ChangesRequiredDecisionDetails.builder()
                        .notes("notes")
                        .requiredChanges(List.of(ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(UUID.randomUUID())).build()))
                        .build())
                    .build())
                .reviewSectionsCompleted(Map.of("abbreviations", Boolean.TRUE))
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        reviewSaveGroupDecisionActionHandler.process(requestTask.getId(),
            RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION,
            pmrvUser,
            requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpCorsiaReviewService, times(1)).saveReviewGroupDecision(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(reviewSaveGroupDecisionActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION);
    }
}