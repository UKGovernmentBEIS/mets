package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2CreationValidationServiceTest {

    @InjectMocks
    private BDRS2CreationValidationService service;

    @Mock
    private BDRS2RequestIdGenerator bdrs2RequestIdGenerator;

    @Mock
    private RequestQueryService requestQueryService;


    @Test
    void validateYear(){
        Long accountId = 1L;
        String requestId = "BDR00178-2025";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .year(Year.of(2025))
                        .build())
                .build();

        when(bdrs2RequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        RequestCreateValidationResult validationResult =
                service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isTrue();

        verify(bdrs2RequestIdGenerator, times(1))
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
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .year(Year.of(2025))
                        .build())
                .build();

        when(bdrs2RequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        RequestCreateValidationResult validationResult =
                service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getReportedRequestTypes()).containsExactlyInAnyOrder(RequestType.BDRS2);

        verify(bdrs2RequestIdGenerator, times(1))
                .generate(params);
        verify(requestQueryService, times(1)).
                existsRequestById(requestId);
    }
}