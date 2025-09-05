package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETICompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;


@Service
public class HSETICompletedCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    protected RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final HSETICompletedRequestActionPayload entityPayload =
                (HSETICompletedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (entityPayload.getOverallDecision() != null) {
            entityPayload.getOverallDecision().setReason(null);
        }

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
}
