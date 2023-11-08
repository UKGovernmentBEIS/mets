package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirCreationValidatorTest {

    @InjectMocks
    private AviationVirCreationValidator virCreationValidatorService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private AviationVirRequestIdGenerator virRequestIdGenerator;

    @Test
    void validate() {

        final String requestId = "VIR-1";
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final VirVerificationData virVerificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1",
                UncorrectedItem.builder()
                    .explanation("Explanation")
                    .reference("A1")
                    .materialEffect(true)
                    .build()))
            .build();
        final RequestParams params = RequestParams.builder()
            .accountId(accountId)
            .requestMetadata(
                AviationVirRequestMetadata.builder().type(RequestMetadataType.AVIATION_VIR).year(year).build())
            .build();

        when(virRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = virCreationValidatorService
            .validate(virVerificationData, accountId, year);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(virRequestIdGenerator, times(1)).generate(params);
        verify(requestQueryService, times(1)).existsRequestById(requestId);
    }
    
    @Test
    void validate_vir_exists() {
        
        final String requestId = "VIR-1";
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final VirVerificationData virVerificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1",
                UncorrectedItem.builder()
                    .explanation("Explanation")
                    .reference("A1")
                    .materialEffect(true)
                    .build()))
            .build();
        final RequestParams params = RequestParams.builder()
            .accountId(accountId)
            .requestMetadata(
                AviationVirRequestMetadata.builder().type(RequestMetadataType.AVIATION_VIR).year(year).build())
            .build();

        when(virRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        // Invoke
        RequestCreateValidationResult result = virCreationValidatorService
            .validate(virVerificationData, accountId, year);

        // Verify
        assertThat(result).isEqualTo(
            RequestCreateValidationResult.builder().valid(false).reportedRequestTypes(Set.of(RequestType.AVIATION_VIR)).build());
        verify(virRequestIdGenerator, times(1)).generate(params);
        verify(requestQueryService, times(1)).existsRequestById(requestId);
    }
}
