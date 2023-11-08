package uk.gov.pmrv.api.notification.template.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;

public interface NotificationTemplateCustomRepository {

    @Transactional(readOnly = true)
    TemplateSearchResults findBySearchCriteria(NotificationTemplateSearchCriteria searchCriteria);
}
