package uk.gov.pmrv.api.workflow.request.core.transform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestActionCustomMapperHandler {

    private final List<RequestActionCustomMapper> mappers;

    public Optional<RequestActionCustomMapper> getMapper(final RequestActionType actionType, final String roleType) {
        
        return mappers.stream().filter(m -> m.getRequestActionType().equals(actionType) &&
                                            m.getUserRoleTypes().contains(roleType))
                      .findFirst();
    }
}
