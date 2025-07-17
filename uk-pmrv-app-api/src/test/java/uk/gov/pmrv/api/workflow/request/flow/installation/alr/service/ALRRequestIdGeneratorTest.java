package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;

import java.time.Year;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRRequestIdGeneratorTest {
    @InjectMocks
    private ALRRequestIdGenerator alrRequestIdGenerator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .year(Year.of(2025))
                        .build())
                .build();

        String requestId = alrRequestIdGenerator.generate(params);

        Assertions.assertEquals("ALR00012-2025", requestId);
    }

    @Test
    void getTypes() {
        assertThat(alrRequestIdGenerator.getTypes()).containsExactly(RequestType.ALR);
    }

    @Test
    void getPrefix() {
        String prefix = alrRequestIdGenerator.getPrefix();

        Assertions.assertEquals("ALR", prefix);
    }
}
