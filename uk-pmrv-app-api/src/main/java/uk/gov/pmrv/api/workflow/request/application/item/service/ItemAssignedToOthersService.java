package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemAssignedToOthersService {

    ItemDTOResponse getItemsAssignedToOthers(AppUser appUser, AccountType accountType, PagingRequest paging);

    String getRoleType();
}
