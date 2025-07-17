package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NerSaveReviewDeterminationActionHandlerTest {

    @InjectMocks
    private NerSaveReviewDeterminationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerReviewValidator reviewValidator;

    @Mock
    private NerApplyReviewService applyService;

    @Test
    void doProcess_validDetermination() {

        final NerProceedToAuthorityDetermination determination =
            NerProceedToAuthorityDetermination.builder().type(NerDeterminationType.PROCEED_TO_AUTHORITY).build();
        final NerSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            NerSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.NER_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .determination(determination)
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).payload(taskPayload).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(reviewValidator.isReviewDeterminationValid(determination,
            taskPayload.getReviewGroupDecisions())).thenReturn(true);

        handler.process(requestTask.getId(),
            RequestTaskActionType.NER_SAVE_REVIEW_DETERMINATION,
            appUser,
            taskActionPayload);

        verify(reviewValidator, times(1)).isReviewDeterminationValid(determination,
            taskPayload.getReviewGroupDecisions());
        verify(applyService, times(1)).saveDetermination(taskActionPayload, requestTask);
    }

    @Test
    void doProcess_invalidDetermination() {

        final NerProceedToAuthorityDetermination determination =
            NerProceedToAuthorityDetermination.builder().type(NerDeterminationType.PROCEED_TO_AUTHORITY).build();
        final NerSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            NerSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.NER_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .determination(determination)
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).payload(taskPayload).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(reviewValidator.isReviewDeterminationValid(determination,
            taskPayload.getReviewGroupDecisions())).thenReturn(false);

        final Long requestTaskIdd = requestTask.getId();
        BusinessException ex = assertThrows(BusinessException.class, () ->
            handler.process(requestTaskIdd,
                RequestTaskActionType.NER_SAVE_REVIEW_DETERMINATION,
                appUser,
                taskActionPayload)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);


        verify(reviewValidator, times(1)).isReviewDeterminationValid(determination,
            taskPayload.getReviewGroupDecisions());
        verify(applyService, never()).saveDetermination(taskActionPayload, requestTask);
    }
}
