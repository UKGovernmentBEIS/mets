package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AviationAerRequestIdGeneratorTest {

    private final AviationAerRequestIdGenerator generator = new AviationAerRequestIdGenerator();

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
            .accountId(7903L)
            .requestMetadata(AviationAerRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER)
                .year(Year.of(2023))
                .build())
            .build();

        String requestId = generator.generate(params);

        assertEquals("AEM07903-2023", requestId);
    }


    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.AVIATION_AER_UKETS, RequestType.AVIATION_AER_CORSIA);
    }

    @Test
    void getPrefix() {
        assertEquals("AEM", generator.getPrefix());
    }
}