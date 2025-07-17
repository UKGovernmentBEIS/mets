package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

public interface RequestCreateByCAValidator<T extends RequestCreateActionPayload> extends RequestCreateValidator {

	@Transactional
    RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, T payload);
}
