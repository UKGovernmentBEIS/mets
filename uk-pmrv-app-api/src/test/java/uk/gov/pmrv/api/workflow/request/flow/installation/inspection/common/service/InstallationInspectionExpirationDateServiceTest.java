package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.FollowUpAction;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionExpirationDateServiceTest {

    @InjectMocks
    private InstallationOnsiteInspectionExpirationDateService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;


    @Test
    void calculateExpirationDate(){
        final String requestId = "INS00045-24";
        final Date expected = DateUtils.atEndOfDay(LocalDate.now().plusDays(10));

        final UUID attachment1 = UUID.randomUUID();
        final FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.NON_COMPLIANCE)
                .explanation("Dummy explanation")
                .followUpActionAttachments(Set.of(attachment1))
                .build();

        final InstallationInspection installationInspection = InstallationInspection.builder()
                .responseDeadline(LocalDate.now().plusDays(10))
                .followUpActions(List.of(followUpAction))
                .build();


        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(attachment1, "att1");

        final Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();
        installationInspectionSectionsCompleted.put("followUpActions",false);

        final Request request = Request.builder()
                .id(requestId)
                .payload(InstallationInspectionRequestPayload
                        .builder()
                        .installationInspection(installationInspection)
                        .inspectionAttachments(attachments)
                        .installationInspectionSectionsCompleted(installationInspectionSectionsCompleted)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        Date actual = service.calculateExpirationDate(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void sendRespondFirstReminderNotification(){
        final String requestId = "INS00045-24";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.INSTALLATION_INSPECTION_OPERATOR_RESPOND.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendRespondFirstReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendRespondSecondReminderNotification(){
        final String requestId = "INS00045-24";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.INSTALLATION_INSPECTION_OPERATOR_RESPOND.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendRespondSecondReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }
}
