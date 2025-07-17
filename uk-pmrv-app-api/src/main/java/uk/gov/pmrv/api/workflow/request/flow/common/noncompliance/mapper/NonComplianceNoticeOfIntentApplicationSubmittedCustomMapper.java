package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload;

@Service
public class NonComplianceNoticeOfIntentApplicationSubmittedCustomMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);


    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload actionPayload =
            (NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload dtoPayload =
            NON_COMPLIANCE_MAPPER.toNoticeOfIntentSubmittedRequestActionIgnoreComments(actionPayload);
        
        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
