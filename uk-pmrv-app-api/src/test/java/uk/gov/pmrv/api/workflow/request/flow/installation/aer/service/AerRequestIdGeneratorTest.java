package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AerRequestIdGeneratorTest {

    @InjectMocks
    private AerRequestIdGenerator generator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(Year.of(2022))
                        .build())
                .build();

        String requestId = generator.generate(params);

        assertEquals("AEM00012-2022", requestId);
    }


    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.AER);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("AEM", prefix);
    }
}
