package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

public interface DocumentTemplateAuthorityInfoProvider {
    CompetentAuthorityEnum getDocumentTemplateCaById(Long templateId);
}
