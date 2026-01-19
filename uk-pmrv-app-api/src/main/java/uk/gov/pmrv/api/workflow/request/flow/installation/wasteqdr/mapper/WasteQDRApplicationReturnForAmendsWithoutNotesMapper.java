package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload;

import java.util.Set;


@Service
public class WasteQDRApplicationReturnForAmendsWithoutNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload entityPayload =
                (WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (entityPayload.getReviewDecision() != null && entityPayload.getReviewDecision().getDetails() != null
                && entityPayload.getReviewDecision().getDetails().getNotes() != null) {
            entityPayload.getReviewDecision().getDetails().setNotes(null);
        }

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
