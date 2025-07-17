package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemService {

    ItemDTOResponse getItemsByRequest(AppUser appUser, String requestId);

    String getRoleType();
}
