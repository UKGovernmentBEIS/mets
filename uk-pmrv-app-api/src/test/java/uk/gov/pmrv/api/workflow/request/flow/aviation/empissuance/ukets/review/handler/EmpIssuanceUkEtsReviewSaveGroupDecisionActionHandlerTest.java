package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsReviewSaveGroupDecisionActionHandler reviewSaveGroupDecisionActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewGroup(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS)
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
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        reviewSaveGroupDecisionActionHandler.process(requestTask.getId(),
            RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_GROUP_DECISION,
            appUser,
            requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpUkEtsReviewService, times(1)).saveReviewGroupDecision(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(reviewSaveGroupDecisionActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_GROUP_DECISION);
    }
}