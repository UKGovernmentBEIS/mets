package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemAccountTypeResponseCreationService {

    ItemDTOResponse toItemDTOResponse(ItemPage itemPage, PmrvUser pmrvUser);

    AccountType getAccountType();
}
