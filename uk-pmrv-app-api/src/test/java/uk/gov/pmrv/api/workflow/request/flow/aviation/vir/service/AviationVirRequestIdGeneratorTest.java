package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class AviationVirRequestIdGeneratorTest {

    @InjectMocks
    private AviationVirRequestIdGenerator generator;

    @Test
    void generate() {

        final RequestParams params = RequestParams.builder()
            .accountId(12L)
            .requestMetadata(AviationVirRequestMetadata.builder()
                .type(RequestMetadataType.VIR)
                .year(Year.of(2022))
                .build())
            .build();

        final String requestId = generator.generate(params);

        assertEquals("VIR00012-2022", requestId);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.AVIATION_VIR);
    }

    @Test
    void getPrefix() {

        final String prefix = generator.getPrefix();

        assertEquals("VIR", prefix);
    }
}
