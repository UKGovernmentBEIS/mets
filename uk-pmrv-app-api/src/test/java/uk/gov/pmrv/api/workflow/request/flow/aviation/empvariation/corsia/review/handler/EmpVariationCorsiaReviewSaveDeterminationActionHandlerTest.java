package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewDeterminationValidatorService;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewSaveDeterminationActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaReviewSaveDeterminationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpVariationCorsiaReviewService empVariationCorsiaReviewService;

    @Mock
    private EmpVariationCorsiaReviewDeterminationValidatorService determinationValidatorService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
                .build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = 
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED)).thenReturn(true);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED);
        verify(empVariationCorsiaReviewService, times(1)).saveDetermination(taskActionPayload, requestTask);
    }

    @Test
    void process_when_invalid_determination_throw_error() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
                .build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = 
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED);
        verifyNoInteractions(empVariationCorsiaReviewService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION);
    }
}
