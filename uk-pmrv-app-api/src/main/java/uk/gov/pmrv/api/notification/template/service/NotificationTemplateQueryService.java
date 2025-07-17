package uk.gov.pmrv.api.notification.template.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.NotificationTemplateAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.transform.NotificationTemplateMapper;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class NotificationTemplateQueryService implements NotificationTemplateAuthorityInfoProvider {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationTemplateMapper notificationTemplateMapper;
    
    NotificationTemplate getNotificationTemplateById(Long id) {
        return notificationTemplateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public TemplateSearchResults getNotificationTemplatesBySearchCriteria(NotificationTemplateSearchCriteria searchCriteria) {
        return notificationTemplateRepository.findBySearchCriteria(searchCriteria);
    }

    @Transactional(readOnly = true)
    public NotificationTemplateDTO getManagedNotificationTemplateById(Long id) {
        return notificationTemplateRepository.findManagedNotificationTemplateByIdWithDocumentTemplates(id)
            .map(notificationTemplateMapper::toNotificationTemplateDTO)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public CompetentAuthorityEnum getNotificationTemplateCaById(Long id) {
        return getNotificationTemplateById(id).getCompetentAuthority();
    }
}
