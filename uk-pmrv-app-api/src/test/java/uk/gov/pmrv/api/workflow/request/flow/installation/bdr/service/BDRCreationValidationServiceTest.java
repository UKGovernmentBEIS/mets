package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRCreationValidationServiceTest {

    @InjectMocks
    private BDRCreationValidationService service;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private BDRRequestIdGenerator bdrRequestIdGenerator;

    @Mock
    private RequestQueryService requestQueryService;


    @Test
    void validateYear(){
        Long accountId = 1L;
        String requestId = "BDR00178-2025";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(BDRRequestMetadata.builder().type(RequestMetadataType.BDR).year(Year.of(2025)).build())
                .build();

        when(bdrRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        RequestCreateValidationResult validationResult = service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isTrue();

        verify(bdrRequestIdGenerator, times(1))
                .generate(params);
        verify(requestQueryService, times(1)).
                existsRequestById(requestId);
    }

    @Test
    void validateYear_bdrForThatYearExists_ReturnFalseValidation(){
        Long accountId = 1L;
        String requestId = "BDR00178-2025";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(BDRRequestMetadata.builder().type(RequestMetadataType.BDR).year(Year.of(2025)).build())
                .build();

        when(bdrRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        RequestCreateValidationResult validationResult = service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getReportedRequestTypes()).containsExactlyInAnyOrder(RequestType.BDR);

        verify(bdrRequestIdGenerator, times(1))
                .generate(params);
        verify(requestQueryService, times(1)).
                existsRequestById(requestId);
    }
}
