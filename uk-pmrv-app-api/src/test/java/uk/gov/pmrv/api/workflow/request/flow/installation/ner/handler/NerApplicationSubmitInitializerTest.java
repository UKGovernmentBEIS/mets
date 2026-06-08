package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class NerApplicationSubmitInitializerTest {

    @InjectMocks
    private NerApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final Long verificationBodyId = 123L;

        final NERVerificationReport verificationReport = NERVerificationReport.builder()
                .verificationBodyId(verificationBodyId)
                .build();

        final NER ner = NER.builder().build();

        final NerRequestPayload nerRequestPayload = NerRequestPayload.builder()
                .ner(ner)
                .nerFileVersion(2)
                .verificationPerformed(true)
                .verificationSectionsCompleted(Map.of("SECTION", List.of(Boolean.TRUE)))
                .verificationReport(verificationReport)
                .build();

        final Request request = Request.builder()
                .payload(nerRequestPayload)
                .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(
                NerApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.NER_APPLICATION_SUBMIT_PAYLOAD)
                        .ner(nerRequestPayload.getNer())
                        .nerFileVersion(nerRequestPayload.getNerFileVersion())
                        .verificationPerformed(nerRequestPayload.isVerificationPerformed())
                        .verificationSectionsCompleted(nerRequestPayload.getVerificationSectionsCompleted())
                        .verificationBodyId(verificationBodyId)
                        .build(),
                requestTaskPayload
        );
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.NER_APPLICATION_SUBMIT);
    }
}
