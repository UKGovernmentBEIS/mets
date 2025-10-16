package uk.gov.pmrv.api.workflow.request.core.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Set;

public interface RequestTypeToRoleMapperService {

    Set<RequestType> getRequestTypes(AppUser appUser);

    String getRoleType();
}
