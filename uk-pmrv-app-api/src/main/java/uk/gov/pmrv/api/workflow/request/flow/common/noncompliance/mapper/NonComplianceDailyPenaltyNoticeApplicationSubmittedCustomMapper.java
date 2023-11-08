package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload;

@Service
public class NonComplianceDailyPenaltyNoticeApplicationSubmittedCustomMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);


    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload actionPayload =
            (NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload dtoPayload =
            NON_COMPLIANCE_MAPPER.toDailyPenaltyNoticeSubmittedRequestActionIgnoreComments(actionPayload);
        
        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
