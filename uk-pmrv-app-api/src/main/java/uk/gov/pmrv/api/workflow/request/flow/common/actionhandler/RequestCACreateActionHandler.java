package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCACreateActionHandler<T extends RequestCreateActionPayload> {

    @Transactional
    String process(CompetentAuthorityEnum ca, T payload, AppUser appUser);

    RequestCreateActionType getRequestCreateActionType();
}
