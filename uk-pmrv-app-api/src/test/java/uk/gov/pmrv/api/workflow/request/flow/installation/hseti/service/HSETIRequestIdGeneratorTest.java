package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIAllocationPeriod;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HSETIRequestIdGeneratorTest {

    @InjectMocks
    private HSETIRequestIdGenerator hsetiRequestIdGenerator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(1L)
                .requestMetadata(HSETIRequestMetadata.builder()
                        .type(RequestMetadataType.HSE_TI)
                        .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                        .build())
                .build();

        String requestId = hsetiRequestIdGenerator.generate(params);

        assertEquals("HSETI00001-2021_2025", requestId);
    }


    @Test
    void getTypes() {
        assertThat(hsetiRequestIdGenerator.getTypes()).containsExactly(RequestType.HSE_TI);
    }

    @Test
    void getPrefix() {
        String prefix = hsetiRequestIdGenerator.getPrefix();

        assertEquals("HSETI", prefix);
    }
}
