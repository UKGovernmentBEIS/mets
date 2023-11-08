package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCreationValidatorServiceTest {

    @InjectMocks
    private AerCreationValidatorService service;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private AerRequestIdGenerator aerRequestIdGenerator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateYear() {
        final Year year = Year.now();
        final Long accountId = 1L;
        final String requestId = "AEM001-2022";
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();

        when(aerRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        // Invoke
        final RequestCreateValidationResult result = service.validateYear(accountId, year);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(requestQueryService, times(1)).existsRequestById(requestId);
        verify(aerRequestIdGenerator, times(1)).generate(params);
    }

    @Test
    void validateYear_year_exists() {
        final Year year = Year.now();
        final Long accountId = 1L;
        final String requestId = "AEM001-2022";
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();
        RequestCreateValidationResult expected = RequestCreateValidationResult.builder()
                .valid(false)
                .reportedRequestTypes(Set.of(RequestType.AER))
                .build();

        when(aerRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        // Invoke
        final RequestCreateValidationResult actual = service.validateYear(accountId, year);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(requestQueryService, times(1)).existsRequestById(requestId);
        verify(aerRequestIdGenerator, times(1)).generate(params);
    }

    @Test
    void validateAccountStatus() {
        final Long accountId = 1L;
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        Set<RequestType> mutuallyExclusiveRequests = Set.of();
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests))
                .thenReturn(validationResult);

        // Invoke
        final RequestCreateValidationResult actual = service.validateAccountStatus(accountId);

        // Verify
        assertThat(actual).isEqualTo(validationResult);
        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);
    }
}
