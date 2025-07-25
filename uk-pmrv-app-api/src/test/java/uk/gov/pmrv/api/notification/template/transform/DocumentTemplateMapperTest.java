package uk.gov.pmrv.api.notification.template.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {DocumentTemplateMapperImpl.class, TemplateInfoMapperImpl.class})
class DocumentTemplateMapperTest {

    @Autowired
    private DocumentTemplateMapper documentTemplateMapper;

    @Test
    void toDocumentTemplateDTO() {
        Long documentTemplateId = 1L;
        String documentTemplateName = "document template name";
        String workflow = " workflow";
        Long firstNotificationTemplateId = 11L;
        PmrvNotificationTemplateName firstNotificationTemplateName = PmrvNotificationTemplateName.EMAIL_CONFIRMATION;
        Long secondNotificationTemplateId = 12L;
        PmrvNotificationTemplateName secondNotificationTemplateName = PmrvNotificationTemplateName.CHANGE_2FA;
        String fileUuid = UUID.randomUUID().toString();
        String filename = "filename";

        NotificationTemplate notificationTemplate1 = createNotificationTemplate(firstNotificationTemplateId, firstNotificationTemplateName);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(secondNotificationTemplateId, secondNotificationTemplateName);
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .id(documentTemplateId)
            .type(DocumentTemplateType.IN_RFI)
            .name(documentTemplateName)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .workflow(workflow)
            .notificationTemplates(Set.of(notificationTemplate1, notificationTemplate2))
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        FileInfoDTO fileDocumentDTO = FileInfoDTO.builder().uuid(fileUuid).name(filename).build();

        TemplateInfoDTO notificationTemplateInfoDTO1 = createTemplateInfoDTO(firstNotificationTemplateId, firstNotificationTemplateName.getName());
        TemplateInfoDTO notificationTemplateInfoDTO2 = createTemplateInfoDTO(secondNotificationTemplateId, secondNotificationTemplateName.getName());

        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDocumentTemplateDTO(documentTemplate, fileDocumentDTO);

        assertNotNull(documentTemplateDTO);
        assertEquals(documentTemplateId, documentTemplateDTO.getId());
        assertEquals(documentTemplateName, documentTemplateDTO.getName());
        assertEquals(workflow, documentTemplateDTO.getWorkflow());
        assertEquals(fileUuid, documentTemplateDTO.getFileUuid());
        assertEquals(filename, documentTemplateDTO.getFilename());
        assertThat(documentTemplateDTO.getNotificationTemplates())
            .hasSize(2)
            .containsExactlyInAnyOrder(notificationTemplateInfoDTO1, notificationTemplateInfoDTO2);
    }

    private NotificationTemplate createNotificationTemplate(Long notificationTemplateId, PmrvNotificationTemplateName name) {
        return NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(name)
            .subject("subject")
            .text("text")
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .workflow("workflow")
            .roleType(RoleTypeConstants.OPERATOR)
            .managed(true)
            .lastUpdatedDate(LocalDateTime.now())
            .build();
    }

    private TemplateInfoDTO createTemplateInfoDTO(Long id, String name) {
        TemplateInfoDTO templateInfoDTO = new TemplateInfoDTO();
        templateInfoDTO.setId(id);
        templateInfoDTO.setName(name);

        return templateInfoDTO;
    }
}