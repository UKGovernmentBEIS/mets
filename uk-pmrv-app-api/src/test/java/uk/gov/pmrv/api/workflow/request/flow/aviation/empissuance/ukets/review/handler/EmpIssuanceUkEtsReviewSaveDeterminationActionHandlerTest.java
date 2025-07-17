package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewDeterminationValidatorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewSaveDeterminationActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsReviewSaveDeterminationActionHandler saveDeterminationActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Mock
    private EmpIssuanceUkEtsReviewDeterminationValidatorService determinationValidatorService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build())
                .build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED)).thenReturn(true);

        saveDeterminationActionHandler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED);
        verify(requestEmpUkEtsReviewService, times(1)).saveDetermination(taskActionPayload, requestTask);
    }

    @Test
    void process_when_invalid_determination_throw_error() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION;
        AppUser appUser = AppUser.builder().build();
        EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build())
                .build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).payload(requestTaskPayload).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(determinationValidatorService.isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> saveDeterminationActionHandler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(determinationValidatorService, times(1)).isValid(requestTaskPayload, EmpIssuanceDeterminationType.APPROVED);
        verifyNoInteractions(requestEmpUkEtsReviewService);
    }

    @Test
    void getTypes() {
        assertThat(saveDeterminationActionHandler.getTypes()).containsExactly(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION);

    }
}