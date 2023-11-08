package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;

public interface RequestCreateByCAValidator<T extends RequestCreateActionPayload> extends RequestCreateValidator {

	@Transactional
    void validateAction(CompetentAuthorityEnum competentAuthority, T payload);
}
