package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.service.PermitIssuanceRequestIdGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRequestIdGeneratorTest {
    @InjectMocks
    private PermitIssuanceRequestIdGenerator generator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(1234L)
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_ISSUANCE)
                        .build())
                .build();

        String requestId = generator.generate(params);

        assertEquals("AEM01234", requestId);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.PERMIT_ISSUANCE);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("AEM", prefix);
    }

}