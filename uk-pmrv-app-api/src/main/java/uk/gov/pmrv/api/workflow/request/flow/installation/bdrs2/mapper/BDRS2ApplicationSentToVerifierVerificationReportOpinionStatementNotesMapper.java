package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;

import java.util.Set;

@Service
public class BDRS2ApplicationSentToVerifierVerificationReportOpinionStatementNotesMapper  implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final BDRS2ApplicationSubmittedRequestActionPayload entityPayload =
                (BDRS2ApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        entityPayload.setVerificationReport(null);

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }


    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.BDRS2_APPLICATION_SENT_TO_VERIFIER;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
