package uk.gov.pmrv.api.notification.template.repository.impl;

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
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class NotificationTemplateCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private NotificationTemplateCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority_with_search_term() {
        String permitWorkflow = "Permit Workflow";
        String accountOpeningWorkflow = "Account Opening Workflow";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        NotificationTemplate notificationTemplate1 = createNotificationTemplate(PmrvNotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT,
            competentAuthority, permitWorkflow, RoleTypeConstants.OPERATOR, accountType, true);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            competentAuthority, permitWorkflow, RoleTypeConstants.OPERATOR, accountType, true);
        NotificationTemplate notificationTemplate3 = createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_CREATED,
            competentAuthority, accountOpeningWorkflow, RoleTypeConstants.OPERATOR, accountType, true);
        createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION, competentAuthority, accountOpeningWorkflow,
            RoleTypeConstants.REGULATOR, accountType, true);
        createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION, CompetentAuthorityEnum.WALES, accountOpeningWorkflow,
            RoleTypeConstants.OPERATOR, accountType, true);
        createNotificationTemplate(PmrvNotificationTemplateName.CHANGE_2FA, null, null, null, accountType, false);
        createNotificationTemplate(PmrvNotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT, competentAuthority, permitWorkflow,
            RoleTypeConstants.OPERATOR, AccountType.AVIATION, true);

        flushAndClear();

        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .competentAuthority(competentAuthority)
            .accountType(accountType)
            .roleType(RoleTypeConstants.OPERATOR)
            .term("account")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        TemplateSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(3);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(3);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                notificationTemplate2.getName().getName(),
                notificationTemplate1.getName().getName(),
                notificationTemplate3.getName().getName()
            );
    }

    @Test
    void findByCompetentAuthority_without_search_term() {
        String permitWorkflow = "Permit Workflow";
        String accountOpeningWorkflow = "Account Opening Workflow";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        createNotificationTemplate(PmrvNotificationTemplateName.INVITATION_TO_EMITTER_CONTACT, competentAuthority, permitWorkflow,
            RoleTypeConstants.OPERATOR, accountType, true);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_CREATED,
            competentAuthority, accountOpeningWorkflow, RoleTypeConstants.REGULATOR, accountType, true);
        NotificationTemplate notificationTemplate3 = createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION,
            competentAuthority, accountOpeningWorkflow, RoleTypeConstants.REGULATOR, accountType, true);
        createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION, CompetentAuthorityEnum.WALES, accountOpeningWorkflow,
            RoleTypeConstants.OPERATOR, accountType, true);
        createNotificationTemplate(PmrvNotificationTemplateName.CHANGE_2FA, null, null, null, accountType ,false);
        createNotificationTemplate(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION, competentAuthority, accountOpeningWorkflow,
            RoleTypeConstants.REGULATOR, AccountType.AVIATION, true);

        flushAndClear();

        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .competentAuthority(competentAuthority)
            .accountType(accountType)
            .roleType(RoleTypeConstants.REGULATOR)
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        TemplateSearchResults
            searchResults = repo.findBySearchCriteria(searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(2);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(2);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                notificationTemplate3.getName().getName(),
                notificationTemplate2.getName().getName()
            );
    }


    private NotificationTemplate createNotificationTemplate(PmrvNotificationTemplateName name, CompetentAuthorityEnum ca, String workflow,
                                                            String roleType, AccountType accountType, boolean managed) {
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .name(name)
            .subject("subject")
            .text("text")
            .competentAuthority(ca)
            .workflow(workflow)
            .roleType(roleType)
            .managed(managed)
            .accountType(accountType)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        entityManager.persist(notificationTemplate);

        return notificationTemplate;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}