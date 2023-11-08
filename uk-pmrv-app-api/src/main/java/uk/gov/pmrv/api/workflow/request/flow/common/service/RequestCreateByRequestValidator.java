package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

public interface RequestCreateByRequestValidator <T extends RequestCreateActionPayload> extends RequestCreateValidator {

    @Transactional
    RequestCreateValidationResult validateAction(Long accountId, T payload);
}
