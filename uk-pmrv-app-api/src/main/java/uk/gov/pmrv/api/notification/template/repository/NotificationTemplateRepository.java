package uk.gov.pmrv.api.notification.template.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.Optional;

/**
 * Repository for {@link NotificationTemplate} objects.
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, NotificationTemplateCustomRepository {

	Optional<NotificationTemplate> findByNameAndCompetentAuthorityAndAccountType(PmrvNotificationTemplateName name,
			CompetentAuthorityEnum competentAuthority, AccountType accountType);

    @EntityGraph(value = "notification-templates-graph", type = EntityGraph.EntityGraphType.FETCH)
    @Query(name = NotificationTemplate.NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID)
    Optional<NotificationTemplate> findManagedNotificationTemplateByIdWithDocumentTemplates(Long id);

    @Query(name = NotificationTemplate.NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID)
    Optional<NotificationTemplate> findManagedNotificationTemplateById(Long id);
}
