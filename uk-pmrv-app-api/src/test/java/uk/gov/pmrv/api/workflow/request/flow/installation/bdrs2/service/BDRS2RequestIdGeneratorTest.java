package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BDRS2RequestIdGeneratorTest {

    @InjectMocks
    private BDRS2RequestIdGenerator bdrs2RequestIdGenerator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .year(Year.of(2025))
                        .build())
                .build();

        String requestId = bdrs2RequestIdGenerator.generate(params);

        assertEquals("BDRS2-00012-2025", requestId);
    }


    @Test
    void getTypes() {
        assertThat(bdrs2RequestIdGenerator.getTypes()).containsExactly(RequestType.BDRS2);
    }

    @Test
    void getPrefix() {
        String prefix = bdrs2RequestIdGenerator.getPrefix();

        assertEquals("BDRS2", prefix);
    }

}