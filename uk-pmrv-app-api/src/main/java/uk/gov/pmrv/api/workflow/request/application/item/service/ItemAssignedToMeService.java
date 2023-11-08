package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemAssignedToMeService {

    ItemDTOResponse getItemsAssignedToMe(PmrvUser pmrvUser, AccountType accountType, PagingRequest paging);

    RoleType getRoleType();
}
