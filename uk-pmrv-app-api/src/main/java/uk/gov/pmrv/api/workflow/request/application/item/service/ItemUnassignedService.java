package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemOrderBy;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;


public interface ItemUnassignedService {

    ItemDTOResponse getUnassignedItems(AppUser appUser, AccountType accountType, PagingRequest paging, ItemOrderBy order,  RequestType requestType, String accountSearchTerm);

    String getRoleType();
}
