package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;

public abstract class BDRS2VerificationReportOpinionStatementNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final BDRS2ApplicationSubmittedRequestActionPayload entityPayload =
                (BDRS2ApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (!ObjectUtils.isEmpty(entityPayload.getVerificationReport()) &&
            !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData()) &&
            !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData().getOpinionStatement())) {

            entityPayload.getVerificationReport().getVerificationData().getOpinionStatement().setNotes(null);
        }


        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }
}
