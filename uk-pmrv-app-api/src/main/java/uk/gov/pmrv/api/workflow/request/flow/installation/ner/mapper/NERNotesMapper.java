package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReportDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewDecisionDetails;

import java.util.Collection;
import java.util.Objects;

public abstract class NERNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final NERApplicationSubmittedRequestActionPayload entityPayload =
                (NERApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (!ObjectUtils.isEmpty(entityPayload.getVerificationReport()) &&
                !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData()) &&
                !ObjectUtils.isEmpty(entityPayload.getVerificationReport().getVerificationData().getOpinionStatement())) {

            entityPayload.getVerificationReport().getVerificationData().getOpinionStatement().setNotes(null);
        }

        if (entityPayload instanceof NERApplicationCompletedRequestActionPayload completedRequestActionPayload
        && completedRequestActionPayload.getRegulatorReviewOutcome() != null) {

            completedRequestActionPayload.getRegulatorReviewOutcome().setNotes(null);

            clearNotes(completedRequestActionPayload.getRegulatorReviewGroupDecisions().values());
        }

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    private void clearNotes(Collection<NERReviewDecision> decisions) {
        decisions.stream()
                .map(this::extractDetails)
                .filter(Objects::nonNull)
                .forEach(details -> details.setNotes(null));
    }

    private NERRegulatorReviewDecisionDetails extractDetails(NERReviewDecision decision) {
        if (decision instanceof NERNerDataRegulatorReviewDecision nerDecision) {
            return nerDecision.getDetails();
        }

        if (decision instanceof NERVerificationReportDataRegulatorReviewDecision verificationDecision) {
            return verificationDecision.getDetails();
        }

        return null;
    }
}
