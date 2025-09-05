package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;

import java.util.Set;

@Service
public class HSETIRegulatorReturnedForAmendsCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final HSETIRegulatorReviewReturnedForAmendsRequestActionPayload entityPayload =
                (HSETIRegulatorReviewReturnedForAmendsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        entityPayload
                .getRegulatorReviewGroupDecisions()
                .get(HSETIReviewGroup.HSETI)
                .getDetails()
                .setCapacityIncreaseDescription(null);

        entityPayload
                .getRegulatorReviewGroupDecisions()
                .get(HSETIReviewGroup.HSETI)
                .getDetails()
                .setCapacityGreaterThanZeroDescription(null);

        entityPayload
                .getRegulatorReviewGroupDecisions()
                .get(HSETIReviewGroup.HSETI)
                .getDetails()
                .setCapacityIncreasePermanence(null);

        entityPayload
                .getRegulatorReviewGroupDecisions()
                .get(HSETIReviewGroup.HSETI)
                .getDetails()
                .setNotes(null);


        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
