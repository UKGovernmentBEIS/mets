package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;

import java.util.Set;

@Service
public class BDRS2ApplicationReturnForAmendsWithoutInternalNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload entityPayload =
                (BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        entityPayload.getRegulatorReviewGroupDecisions().values().stream()
                .filter(decision -> decision.getReviewDataType().equals(BDRS2ReviewDataType.BDRS2_DATA))
                .map(BDRS2Bdrs2DataRegulatorReviewDecision.class::cast)
                .filter(decision -> decision.getDetails() != null)
                .forEach(decision -> decision.getDetails().setNotes(null));

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
