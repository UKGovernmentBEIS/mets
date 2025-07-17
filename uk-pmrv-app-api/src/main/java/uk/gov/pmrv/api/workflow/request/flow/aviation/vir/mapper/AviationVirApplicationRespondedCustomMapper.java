package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;

@Service
public class AviationVirApplicationRespondedCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        
        final AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload entityPayload =
                (AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);
        final AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload dtoPayload = 
            VIR_MAPPER.cloneRespondedPayloadIgnoreComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
