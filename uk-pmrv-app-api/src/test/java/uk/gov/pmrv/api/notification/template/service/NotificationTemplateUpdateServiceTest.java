package uk.gov.pmrv.api.notification.template.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateUpdateServiceTest {

    @InjectMocks
    private NotificationTemplateUpdateService notificationTemplateUpdateService;

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @Test
    void updateNotificationTemplate() {
        Long notificationTemplateId = 1L;
        String updatedNotificationTemplateSubject = "updated subject";
        String updatedNotificationTemplateText = "updated text";
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(NotificationTemplateName.EMAIL_CONFIRMATION)
            .subject("subject")
            .text("text")
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .roleType(RoleType.OPERATOR)
            .managed(true)
            .accountType(AccountType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject(updatedNotificationTemplateSubject)
            .text(updatedNotificationTemplateText)
            .build();

        when(notificationTemplateRepository.findManagedNotificationTemplateById(notificationTemplateId))
            .thenReturn(Optional.of(notificationTemplate));

        notificationTemplateUpdateService.updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO);

        assertEquals(updatedNotificationTemplateSubject, notificationTemplateUpdateDTO.getSubject());
        assertEquals(updatedNotificationTemplateText, notificationTemplateUpdateDTO.getText());
    }

    @Test
    void updateNotificationTemplate_not_found() {
        Long notificationTemplateId = 1L;
        String updatedNotificationTemplateSubject = "updated subject";
        String updatedNotificationTemplateText = "updated text";
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject(updatedNotificationTemplateSubject)
            .text(updatedNotificationTemplateText)
            .build();

        when(notificationTemplateRepository.findManagedNotificationTemplateById(notificationTemplateId))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
            notificationTemplateUpdateService.updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(notificationTemplateRepository, times(1))
            .findManagedNotificationTemplateById(notificationTemplateId);
        verifyNoMoreInteractions(notificationTemplateRepository);
    }
}