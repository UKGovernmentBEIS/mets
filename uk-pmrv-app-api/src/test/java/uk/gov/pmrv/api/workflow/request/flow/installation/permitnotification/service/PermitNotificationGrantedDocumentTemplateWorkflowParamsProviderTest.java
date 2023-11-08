package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGrantedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitNotificationGrantedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED);
    }

    @Test
    void constructParams() {

        final PermitNotificationRequestPayload payload = PermitNotificationRequestPayload.builder()
            .reviewDecision(PermitNotificationReviewDecision.builder().type(PermitNotificationReviewDecisionType.ACCEPTED).details(
                PermitNotificationAcceptedDecisionDetails.builder()
                    .officialNotice("the official notice")
                    .followUp(FollowUp.builder()
                        .followUpRequest("the request")
                        .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                        .build())
                    .build()
            ).build())
            .build();
        String requestId = "1";
        
        final Map<String, Object> result = provider.constructParams(payload, requestId);
        assertThat(result).containsExactlyEntriesOf(Map.of("officialNotice", "the official notice"));
    }

}
