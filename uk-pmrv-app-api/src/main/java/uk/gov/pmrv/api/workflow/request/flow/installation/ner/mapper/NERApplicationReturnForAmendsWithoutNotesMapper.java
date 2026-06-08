package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReportDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewDecisionDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
public class NERApplicationReturnForAmendsWithoutNotesMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final NERRegulatorReviewReturnedForAmendsRequestActionPayload entityPayload =
                (NERRegulatorReviewReturnedForAmendsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO =
                requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        clearNotes(entityPayload.getRegulatorReviewGroupDecisions().values());

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
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
