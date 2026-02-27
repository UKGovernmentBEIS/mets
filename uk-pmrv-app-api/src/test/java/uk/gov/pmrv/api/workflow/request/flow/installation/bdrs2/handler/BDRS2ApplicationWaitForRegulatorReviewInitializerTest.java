package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2InitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationWaitForRegulatorReviewInitializerTest {

    @InjectMocks
    private BDRS2ApplicationWaitForRegulatorReviewInitializer initializer;

    @Test
    void initializePayload_whenInitiatedType_shouldSendEmailNotification() {
        final BDRS2RequestMetadata metadata = BDRS2RequestMetadata.builder()
                .bdrs2InitiationType(BDRS2InitiationType.INITIATED)
                .build();

        final Request request = Request.builder()
                .metadata(metadata)
                .payload(BDRS2RequestPayload.builder()
                        .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(result).isInstanceOf(BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload.class);
        BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload payload =
                (BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload) result;

        assertEquals(RequestTaskPayloadType.BDRS2_APPLICATION_WAIT_FOR_REGULATOR_REVIEW_PAYLOAD,
                payload.getPayloadType());
        assertThat(payload.isSendEmailNotification()).isTrue();
    }

    @Test
    void initializePayload_whenReInitiatedType_shouldNotSendEmailNotification() {
        final BDRS2RequestMetadata metadata = BDRS2RequestMetadata.builder()
                .bdrs2InitiationType(BDRS2InitiationType.RE_INITIATED)
                .build();

        final Request request = Request.builder()
                .metadata(metadata)
                .payload(BDRS2RequestPayload.builder()
                        .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(result).isInstanceOf(BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload.class);
        BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload payload =
                (BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload) result;

        assertEquals(RequestTaskPayloadType.BDRS2_APPLICATION_WAIT_FOR_REGULATOR_REVIEW_PAYLOAD,
                payload.getPayloadType());
        assertThat(payload.isSendEmailNotification()).isFalse();
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();

        assertEquals(Set.of(RequestTaskType.BDRS2_WAIT_FOR_REGULATOR_REVIEW), result);
    }
}
