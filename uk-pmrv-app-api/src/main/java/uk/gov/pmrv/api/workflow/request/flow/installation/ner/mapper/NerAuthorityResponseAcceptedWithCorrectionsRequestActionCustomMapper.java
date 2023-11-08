package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@Service
public class NerAuthorityResponseAcceptedWithCorrectionsRequestActionCustomMapper
    extends AbstractNerAuthorityResponseAcceptedRequestActionCustomMapper {

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_APPLICATION_ACCEPTED_WITH_CORRECTIONS;
    }
}
