package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCreateActionResourceTypeHandler<T extends RequestCreateActionPayload> {

    @Transactional
    String process(@NotNull String resourceId, RequestCreateActionType requestCreateActionType, T payload, AppUser appUser);

    String getResourceType();

}
