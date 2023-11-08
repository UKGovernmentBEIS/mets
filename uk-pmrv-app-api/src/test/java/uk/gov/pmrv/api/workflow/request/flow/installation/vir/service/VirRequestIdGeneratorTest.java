package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRequestIdGenerator;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class VirRequestIdGeneratorTest {

    @InjectMocks
    private VirRequestIdGenerator generator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(VirRequestMetadata.builder()
                        .type(RequestMetadataType.VIR)
                        .year(Year.of(2022))
                        .build())
                .build();

        String requestId = generator.generate(params);

        assertEquals("VIR00012-2022", requestId);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.VIR);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("VIR", prefix);
    }
}
