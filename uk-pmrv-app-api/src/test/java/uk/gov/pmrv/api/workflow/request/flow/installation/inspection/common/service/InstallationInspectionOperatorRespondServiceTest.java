package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOperatorRespondService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionOperatorRespondServiceTest {

    @InjectMocks
    private InstallationOnsiteInspectionOperatorRespondService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private WebAppProperties webAppProperties;

    @Test
    void applySaveAction(){
        final Set<UUID> followUpActionResponseAttachments = new HashSet<>();
        final FollowUpActionResponse followUpActionResponse = FollowUpActionResponse
                .builder()
                .followUpActionResponseAttachments(followUpActionResponseAttachments)
                .completed(true)
                .explanation("Test")
                .completionDate(LocalDate.now().minusDays(2))
                .build();


        final InstallationInspectionOperatorRespondRequestTaskPayload expectedTaskPayload =
                InstallationInspectionOperatorRespondRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final InstallationInspectionOperatorRespondSaveRequestTaskActionPayload expectedTaskActionPayload =
                InstallationInspectionOperatorRespondSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE_PAYLOAD)
                        .followupActionsResponses(Map.of(0, followUpActionResponse))
                        .installationInspectionOperatorRespondSectionsCompleted(Map.of())
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getFollowupActionsResponses(), expectedTaskActionPayload.getFollowupActionsResponses());
        assertEquals(expectedTaskPayload.getInstallationInspectionOperatorRespondSectionsCompleted(),
                expectedTaskActionPayload.getInstallationInspectionOperatorRespondSectionsCompleted());
    }

    @Test
    void applySubmitAction(){
        final String userId = "userId";

        final long accountId = 1L;
        final String regulator = "regulator";
        final Set<UUID> followUpActionResponseAttachments = new HashSet<>();
        final FollowUpActionResponse followUpActionResponse = FollowUpActionResponse
                .builder()
                .followUpActionResponseAttachments(followUpActionResponseAttachments)
                .completed(true)
                .explanation("Test")
                .completionDate(LocalDate.now().minusDays(2))
                .build();

        final UUID attachment1 = UUID.randomUUID();

        final FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.NON_COMPLIANCE)
                .explanation("Dummy explanation")
                .followUpActionAttachments(Set.of(attachment1))
                .build();

        final InstallationInspection installationInspection = InstallationInspection.builder()
                .responseDeadline(LocalDate.now().plusDays(1))
                .followUpActions(List.of(followUpAction))
                .build();
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final InstallationInspectionOperatorRespondRequestTaskPayload taskPayload =
            InstallationInspectionOperatorRespondRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD)
                .installationInspection(installationInspection)
                .followupActionsResponses(Map.of(0, followUpActionResponse))
                .inspectionAttachments(Map.of(attachment1, "att1"))
                .installationInspectionOperatorRespondSectionsCompleted(Map.of("followUpActionsResponses",true))
                .build();

        final Request request = Request.builder()
            .accountId(accountId)
            .payload(InstallationInspectionRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PAYLOAD)
                .regulatorReviewer(regulator)
                .installationInspectionSectionsCompleted(Map.of("followUpActions",false))
                .build())
            .build();


        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(taskPayload)
            .build();


        final InstallationInspectionOperatorRespondedRequestActionPayload respondedActionPayload =
            InstallationInspectionOperatorRespondedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPONDED_PAYLOAD)
                .installationInspection(installationInspection)
                .followupActionsResponses(Map.of(0,followUpActionResponse))
                .inspectionAttachments(Map.of(attachment1, "att1"))
                .build();

        // Invoke
        service.applySubmitAction(requestTask, appUser);

        verify(requestService, times(1))
            .addActionToRequest(request, respondedActionPayload,
                RequestActionType.INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED, userId);

        assertThat(requestTask.getPayload()).isInstanceOf(
            InstallationInspectionOperatorRespondRequestTaskPayload.class);

        InstallationInspectionRequestPayload payloadSaved =
            (InstallationInspectionRequestPayload) requestTask.getRequest().getPayload();
        assertThat(payloadSaved.getFollowupActionsResponses()).containsExactlyEntriesOf(Map.of(0,followUpActionResponse));
        assertThat(payloadSaved.getInstallationInspectionOperatorRespondSectionsCompleted()).containsAllEntriesOf(Map.of("followUpActionsResponses",true));
        assertThat(payloadSaved.getInspectionAttachments()).containsAllEntriesOf(Map.of(attachment1, "att1"));
    }
}
