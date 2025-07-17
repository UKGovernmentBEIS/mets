package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import java.util.Set;

@Service
public class ALRApplicationVerificationSubmittedVerificationReportOpinionStatementNotesMapper extends ALRVerificationReportOpinionStatementNotesMapper {

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.ALR_APPLICATION_VERIFICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
