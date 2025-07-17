package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewDeterminationValidatorServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Spy
    private ArrayList<EmpIssuanceCorsiaReviewDeterminationTypeValidator> validators;

    @Mock
    private EmpIssuanceCorsiaReviewDeterminationApprovedValidator reviewDeterminationApprovedValidator;

    @BeforeEach
    void setup() {
        validators.add(reviewDeterminationApprovedValidator);
    }

    @Test
    void isValid() {
        EmpIssuanceDeterminationType determinationType = EmpIssuanceDeterminationType.APPROVED;
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder().build();

        when(reviewDeterminationApprovedValidator.getType()).thenReturn(EmpIssuanceDeterminationType.APPROVED);
        when(reviewDeterminationApprovedValidator.isValid(requestTaskPayload)).thenReturn(true);

        assertTrue(reviewDeterminationValidatorService.isValid(requestTaskPayload, determinationType));

        verify(reviewDeterminationApprovedValidator, times(1)).getType();
        verify(reviewDeterminationApprovedValidator, times(1)).isValid(requestTaskPayload);
    }

    @Test
    void isValid_when_no_Validator_found_throws_error() {
        EmpIssuanceDeterminationType determinationType = EmpIssuanceDeterminationType.APPROVED;
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder().build();

        when(reviewDeterminationApprovedValidator.getType()).thenReturn(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN);

        BusinessException be = assertThrows(BusinessException.class, () -> {
            reviewDeterminationValidatorService.isValid(requestTaskPayload, determinationType);
        });

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(reviewDeterminationApprovedValidator, times(1)).getType();
        verify(reviewDeterminationApprovedValidator, never()).isValid(any());
    }
}