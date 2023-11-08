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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceDailyPenaltyNoticeInitializerTest {

    @InjectMocks
    private NonComplianceDailyPenaltyNoticeInitializer initializer;

    @Test
    void initializePayload() {

        final UUID dailyPenaltyNotice = UUID.randomUUID();
        final Request request = Request.builder().payload(NonComplianceRequestPayload.builder()
            .dailyPenaltyNotice(dailyPenaltyNotice).build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceDailyPenaltyNoticeRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD)
            .dailyPenaltyNotice(dailyPenaltyNotice)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE,
            RequestTaskType.AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE);
    }

}
