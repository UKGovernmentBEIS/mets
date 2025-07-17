package uk.gov.pmrv.api.notification.template.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class NotificationTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private NotificationTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findByNameAndCompetentAuthorityAndAccountType() {
        Optional<NotificationTemplate> result = repo.findByNameAndCompetentAuthorityAndAccountType(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthorityEnum.ENGLAND,
            AccountType.INSTALLATION);
        assertThat(result).isEmpty();
        
        NotificationTemplate notificationTemplate = createNotificationTemplate(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
                CompetentAuthorityEnum.ENGLAND,
                "subject",
                "content",
                "workflow",
                true,
                AccountType.INSTALLATION);
        
        entityManager.persist(notificationTemplate);
        flushAndClear();
        
        result = repo.findByNameAndCompetentAuthorityAndAccountType(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthorityEnum.ENGLAND,
            AccountType.INSTALLATION);
        assertThat(result).isNotEmpty();
    }
    
    @Test
    void findByNameAndCompetentAuthorityAndAccountType_empty_ca_and_account_type() {
        Optional<NotificationTemplate> result = repo.findByNameAndCompetentAuthorityAndAccountType(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, null, null);
        assertThat(result).isEmpty();
        
        NotificationTemplate notificationTemplate = createNotificationTemplate(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
                null,
                "subject",
                "content",
                "workflow",
                true,
                null);
        
        entityManager.persist(notificationTemplate);
        flushAndClear();
        
        result = repo.findByNameAndCompetentAuthorityAndAccountType(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, null, null);
        assertThat(result).isNotEmpty();
    }

    @Test
    void findManagedNotificationTemplateByIdWithDocumentTemplates() {
        String permitWorkflow = "Permit Workflow";
        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthorityEnum.WALES,
            "Invitation To Operator Account",
            "Invitation To Operator Account",
            permitWorkflow,
            true,
            AccountType.INSTALLATION);

        DocumentTemplate documentTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "Operator Account Doc", CompetentAuthorityEnum.WALES, permitWorkflow, AccountType.INSTALLATION, 1L);

        addDocumentTemplateToNotificationTemplate(documentTemplate1, notificationTemplate1);

        NotificationTemplate notificationTemplate2 = createNotificationTemplate(
        		PmrvNotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT,
            CompetentAuthorityEnum.WALES,
            "Invitation To Verifier Account",
            "Invitation To Verifier Account",
            permitWorkflow,
            true,
            AccountType.INSTALLATION);

        DocumentTemplate documentTemplate2 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "Verifier Account Doc", CompetentAuthorityEnum.WALES, permitWorkflow, AccountType.INSTALLATION, 2L);

        addDocumentTemplateToNotificationTemplate(documentTemplate2, notificationTemplate2);

        entityManager.persist(notificationTemplate1);
        entityManager.persist(notificationTemplate2);
        entityManager.persist(documentTemplate1);
        entityManager.persist(documentTemplate2);

        flushAndClear();

        Optional<NotificationTemplate> optionalNotificationTemplate =
            repo.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplate1.getId());

        assertThat(optionalNotificationTemplate).isNotEmpty();

        NotificationTemplate notificationTemplate = optionalNotificationTemplate.get();
        assertEquals(PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, notificationTemplate.getName());

        Set<DocumentTemplate> documentTemplates = notificationTemplate.getDocumentTemplates();
        assertThat(documentTemplates).hasSize(1);
        assertThat(documentTemplates).containsExactlyInAnyOrder(documentTemplate1);
    }

    @Test
    void findManagedNotificationTemplateByIdWithDocumentTemplates_not_managed() {
        String permitWorkflow = "Permit Workflow";
        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
        		PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthorityEnum.WALES,
            "Invitation To Operator Account",
            "Invitation To Operator Account",
            permitWorkflow,
            false,
            AccountType.INSTALLATION);

        entityManager.persist(notificationTemplate1);

        flushAndClear();

        Optional<NotificationTemplate> optionalNotificationTemplate =
            repo.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplate1.getId());

        assertThat(optionalNotificationTemplate).isEmpty();
    }

    private NotificationTemplate createNotificationTemplate(PmrvNotificationTemplateName name, CompetentAuthorityEnum ca, String subject, String text,
                                                            String workflow, boolean managed, AccountType accountType) {
        return NotificationTemplate.builder()
            .name(name)
            .subject(subject)
            .text(text)
            .competentAuthority(ca)
            .workflow(workflow)
            .roleType(RoleTypeConstants.OPERATOR)
            .managed(managed)
            .accountType(accountType)
            .lastUpdatedDate(LocalDateTime.now())
            .build();
    }

    private DocumentTemplate createDocumentTemplate(DocumentTemplateType type, String name, CompetentAuthorityEnum ca, String workflow, AccountType accountType, Long fileDocumentTemplateId) {
        return DocumentTemplate.builder()
                .type(type)
                .name(name)
                .competentAuthority(ca)
                .workflow(workflow)
                .accountType(accountType)
                .lastUpdatedDate(LocalDateTime.now())
                .fileDocumentTemplateId(fileDocumentTemplateId)
                .build();
    }

    private void addDocumentTemplateToNotificationTemplate(DocumentTemplate documentTemplate, NotificationTemplate notificationTemplate) {
        notificationTemplate.getDocumentTemplates().add(documentTemplate);
        documentTemplate.getNotificationTemplates().add(notificationTemplate);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}