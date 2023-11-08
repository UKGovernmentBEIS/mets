package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentInitializerTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentInitializer initializer;

    @Test
    void initializePayload() {

        final UUID noticeOfIntent = UUID.randomUUID();
        final Request request = Request.builder().payload(NonComplianceRequestPayload.builder()
            .noticeOfIntent(noticeOfIntent).build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceNoticeOfIntentRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD)
            .noticeOfIntent(noticeOfIntent)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT,
            RequestTaskType.AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT);
    }

}
