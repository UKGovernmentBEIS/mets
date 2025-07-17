package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionRequestIdGeneratorTest {

    @InjectMocks
    private InstallationOnsiteInspectionRequestIdGenerator generator;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(1234L)
                .requestMetadata(InstallationInspectionRequestMetadata.builder()
                        .type(RequestMetadataType.INSTALLATION_INSPECTION)
                        .build())
                .build();

        String requestId = generator.generate(params);

        assertEquals("INS01234-1", requestId);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.INSTALLATION_ONSITE_INSPECTION);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("INS", prefix);
    }
}
