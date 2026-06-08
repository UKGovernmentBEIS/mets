package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;

import java.util.Set;

@Service
public class NERApplicationSentToVerifierNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final NERApplicationSubmittedRequestActionPayload entityPayload =
                (NERApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (entityPayload.getVerificationReport() != null && entityPayload.getVerificationReport().getVerificationData() != null
        && entityPayload.getVerificationReport().getVerificationData().getOpinionStatement() != null) {
            entityPayload.getVerificationReport().getVerificationData().getOpinionStatement().setNotes(null);
        }

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }


    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
