package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

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

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewDeterminationValidatorService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewSaveDeterminationActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsReviewSaveDeterminationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpVariationUkEtsReviewService empVariationUkEtsReviewService;

    @Mock
    private EmpVariationUkEtsReviewDeterminationValidatorService determinationValidatorService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
                .build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED)).thenReturn(true);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED);
        verify(empVariationUkEtsReviewService, times(1)).saveDetermination(taskActionPayload, requestTask);
    }

    @Test
    void process_when_invalid_determination_throw_error() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
        		EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build())
                .build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpVariationDeterminationType.APPROVED);
        verifyNoInteractions(empVariationUkEtsReviewService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION);
    }
}
