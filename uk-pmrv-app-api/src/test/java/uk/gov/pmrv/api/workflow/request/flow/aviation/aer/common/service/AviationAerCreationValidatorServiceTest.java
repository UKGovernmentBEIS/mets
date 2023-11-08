package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCreationValidatorServiceTest {

    @InjectMocks
    private AviationAerCreationValidatorService validatorService;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private AviationAerRequestIdGenerator aviationAerRequestIdGenerator;

    @Test
    void validateAccountStatus() {
        Long accountId = 1L;
        Set<AccountStatus> accountStatuses = Set.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE);
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(requestCreateValidatorService.validate(accountId, accountStatuses, Set.of())).thenReturn(validationResult);

        assertEquals(validationResult, validatorService.validateAccountStatus(accountId));

        verify(requestCreateValidatorService, times(1)).validate(accountId, accountStatuses, Set.of());
    }

    @Test
    void validateReportingYear() {
        Long accountId = 1L;
        Year reportingYear = Year.of(2023);
        String requestId = "REQ-ID-1";

        RequestParams params = RequestParams.builder()
            .accountId(accountId)
            .requestMetadata(AviationAerRequestMetadata.builder().type(RequestMetadataType.AVIATION_AER).year(reportingYear).build())
            .build();

        when(aviationAerRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        assertEquals(validationResult, validatorService.validateReportingYear(accountId, reportingYear));
    }
}