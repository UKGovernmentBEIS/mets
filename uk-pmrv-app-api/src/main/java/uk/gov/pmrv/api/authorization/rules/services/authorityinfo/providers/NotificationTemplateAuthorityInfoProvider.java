package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

public interface NotificationTemplateAuthorityInfoProvider {
    CompetentAuthorityEnum getNotificationTemplateCaById(Long templateId);

}
