package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import java.util.Set;

@Service
public class NERApplicationWithdrawnNotesMapper extends NERNotesMapper {

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
