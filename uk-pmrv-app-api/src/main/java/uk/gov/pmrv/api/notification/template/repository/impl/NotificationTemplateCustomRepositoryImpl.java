package uk.gov.pmrv.api.notification.template.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateCustomRepository;

@Repository
public class NotificationTemplateCustomRepositoryImpl implements NotificationTemplateCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TemplateSearchResults findBySearchCriteria(NotificationTemplateSearchCriteria searchCriteria) {
        return TemplateSearchResults.builder()
            .templates(constructResultsQuery(searchCriteria).getResultList())
            .total(((Number) constructCountQuery(searchCriteria).getSingleResult()).longValue())
            .build();
    }

    private Query constructResultsQuery(NotificationTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append(constructMainQueryStatement(searchCriteria))
            .append("order by name asc \n")
            .append("limit :limit \n")
            .append("offset :offset \n");

        return createQuery(sb.toString(), searchCriteria, false);
    }

    private Query constructCountQuery(NotificationTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append("select count(*) from ( \n")
            .append(constructMainQueryStatement(searchCriteria))
            .append(") results");

        return createQuery(sb.toString(), searchCriteria, true);
    }

    private String constructMainQueryStatement(NotificationTemplateSearchCriteria searchCriteria) {
        StringBuilder sb = new StringBuilder();

        sb.append("select id, name, workflow, last_updated_date as lastUpdatedDate \n")
            .append("from notification_template \n")
            .append("where is_managed = :managed \n")
            .append("and competent_authority = :competentAuthority \n")
            .append("and role_type = :roleType \n")
            .append("and account_type = :accountType \n")
            .append(StringUtils.hasText(searchCriteria.getTerm()) ? "and (name ilike :term or workflow ilike :term) \n" : "\n");

        return sb.toString();
    }

    private Query createQuery(String sqlStatement, NotificationTemplateSearchCriteria searchCriteria, boolean forCount) {
        Query query = forCount
            ? entityManager.createNativeQuery(sqlStatement)
            : entityManager.createNativeQuery(sqlStatement, NotificationTemplate.NOTIFICATION_TEMPLATE_INFO_DTO_RESULT_MAPPER);

        query.setParameter("managed", true);
        query.setParameter("competentAuthority", searchCriteria.getCompetentAuthority().name());
        query.setParameter("roleType", searchCriteria.getRoleType());
        query.setParameter("accountType", searchCriteria.getAccountType().name());

        if(StringUtils.hasText(searchCriteria.getTerm())) {
            query.setParameter("term", "%" + searchCriteria.getTerm() + "%");
        }

        if(!forCount) {
            query.setParameter("limit", searchCriteria.getPaging().getPageSize());
            query.setParameter("offset", searchCriteria.getPaging().getPageNumber() * searchCriteria.getPaging().getPageSize());
        }

        return query;
    }
}
