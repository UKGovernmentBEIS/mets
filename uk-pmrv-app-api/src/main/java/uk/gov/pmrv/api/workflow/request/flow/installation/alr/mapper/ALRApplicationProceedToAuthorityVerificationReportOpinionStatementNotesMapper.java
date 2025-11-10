package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationProceededToAuthorityRequestActionPayload;

@Service
public class ALRApplicationProceedToAuthorityVerificationReportOpinionStatementNotesMapper implements RequestActionCustomMapper {


    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final ALRApplicationProceededToAuthorityRequestActionPayload entityPayload =
            (ALRApplicationProceededToAuthorityRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (!ObjectUtils.isEmpty(entityPayload.getVerificationReport()) &&
            !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData()) &&
            !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData().getOpinionStatement())) {

            entityPayload.getVerificationReport().getVerificationData().getOpinionStatement().setNotes(null);
        }

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
