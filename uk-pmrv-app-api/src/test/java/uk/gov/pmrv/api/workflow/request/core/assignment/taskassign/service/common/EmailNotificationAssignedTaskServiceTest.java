package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

public class EmailNotificationAssignedTaskServiceTest {

    @InjectMocks
    private EmailNotificationAssignedTaskService emailNotificationAssignedTaskService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private UserAuthService userAuthService;


    @Mock
    private WebAppProperties webAppProperties;

    private static final String USER_ID = "userId";
    private static final String EMAIL = "email@example.com";
    private static final String HOME_PAGE = "https://www.example.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webAppProperties.getUrl()).thenReturn(HOME_PAGE);
    }

    @Test
    public void sendEmailToRecipient_shouldCallNotifyRecipient_whenUserIdNotNull() {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(EMAIL);
        when(userAuthService.getUserByUserId(USER_ID)).thenReturn(userInfoDTO);

        emailNotificationAssignedTaskService.sendEmailToRecipient(USER_ID);

        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class),
            eq(EMAIL));
    }
}
