package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BDRRequestIdGeneratorTest {

    @InjectMocks
    private BDRRequestIdGenerator bdrRequestIdGenerator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .year(Year.of(2025))
                        .build())
                .build();

        String requestId = bdrRequestIdGenerator.generate(params);

        assertEquals("BDR00012-2025", requestId);
    }


    @Test
    void getTypes() {
        assertThat(bdrRequestIdGenerator.getTypes()).containsExactly(RequestType.BDR);
    }

    @Test
    void getPrefix() {
        String prefix = bdrRequestIdGenerator.getPrefix();

        assertEquals("BDR", prefix);
    }
}
