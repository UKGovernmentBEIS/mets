package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaInitiateValidatorServiceTest {

    @InjectMocks
    private AviationDoECorsiaInitiateValidatorService validator;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(Set.of(
                AviationAccountStatus.NEW,
                AviationAccountStatus.LIVE));
    }

    @Test
    void getReferableRequestType() {
        assertThat(validator.getReferableRequestType()).isEqualTo(RequestType.AVIATION_AER_CORSIA);
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.AVIATION_DOE_CORSIA);
    }

    @Test
    void validateRequestType() {
        Long accountId = 1L;

        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
             "aer",
             RequestType.AVIATION_AER_CORSIA,
             RequestStatus.COMPLETED,
             LocalDateTime.now(),
             aerRequestMetadata);

        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_DOE_CORSIA,
                RequestStatus.IN_PROGRESS, accountId, aerRequestMetadata.getYear())).thenReturn(false);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        verify(requestQueryService, times(1)).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(  RequestType.AVIATION_DOE_CORSIA,
                RequestStatus.IN_PROGRESS, accountId, aerRequestMetadata.getYear());
    }

    @Test
    void validateRequestType_nullMetadata_throwBusinessException() {
        Long accountId = 1L;

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
             "aer",
             RequestType.AVIATION_AER_CORSIA,
             RequestStatus.COMPLETED,
             LocalDateTime.now(),
             null);


        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateRequestType(accountId, requestDetailsDTO));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verifyNoInteractions(requestQueryService);
    }

    @Test
    void validateRequestType_doerExistsForAccountIdAndYear_returnInvalidResult() {
        Long accountId = 1L;

        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().build();

        final RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
             "aer",
             RequestType.AVIATION_AER_CORSIA,
             RequestStatus.COMPLETED,
             LocalDateTime.now(),
             aerRequestMetadata);

        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_DOE_CORSIA,
                RequestStatus.IN_PROGRESS, accountId, aerRequestMetadata.getYear())).thenReturn(true);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.AVIATION_DOE_CORSIA);
        verify(requestQueryService, times(1)).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(  RequestType.AVIATION_DOE_CORSIA,
                RequestStatus.IN_PROGRESS, accountId, aerRequestMetadata.getYear());
    }
}
