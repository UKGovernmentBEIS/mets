package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERApplicationWaitForAmendsInitializerTest {

    @InjectMocks
    private NERApplicationWaitForAmendsInitializer initializer;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Test
    void initializePayload() {
        Long accountId = 1L;
        String requestId = "NER-00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.NER)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(NerRequestPayload
                        .builder()
                        .ner(NER
                                .builder()
                                .build())
                        .verificationReport(NERVerificationReport.builder().build())
                        .build()
                )
                .metadata(NERRequestMetadata.builder().build())
                .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = NERApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.NER_WAIT_FOR_AMENDS_PAYLOAD)
                .ner(((NerRequestPayload) request.getPayload()).getNer())
                .verificationReport(((NerRequestPayload) request.getPayload()).getVerificationReport())
                .nerFileVersion(((NerRequestPayload) request.getPayload()).getNerFileVersion())
                .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);

        verify(requestVerificationService, times(1))
                .refreshVerificationReportVBDetails(((NerRequestPayload) request.getPayload()).getVerificationReport(), request.getVerificationBodyId());
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.NER_WAIT_FOR_AMENDS);
    }
}
