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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewedRequestActionPayload;

@Service
public class AviationVirApplicationReviewedCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {
        
        final AviationVirApplicationReviewedRequestActionPayload entityPayload =
                (AviationVirApplicationReviewedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);
        final AviationVirApplicationReviewedRequestActionPayload dtoPayload = VIR_MAPPER.cloneReviewedPayloadIgnoreComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AVIATION_VIR_APPLICATION_REVIEWED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
