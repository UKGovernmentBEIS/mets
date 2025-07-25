package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceReviewService;

@ExtendWith(MockitoExtension.class)
class PermitReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload payload =
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .group(PermitReviewGroup.INSTALLATION_DETAILS)
                .decision(PermitIssuanceReviewDecision.builder()
                    .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(ChangesRequiredDecisionDetails.builder().notes("notes")
                        .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired", Set.of(UUID.randomUUID())))).build())
                    .build()
                )
                .build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        final AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION,
            appUser,
            payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitIssuanceReviewService, times(1)).saveReviewGroupDecision(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION);
    }
}