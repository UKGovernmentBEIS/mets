package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemResponseService {

    private final List<ItemAccountTypeResponseCreationService> itemResponseCreationServices;

    public ItemDTOResponse toItemDTOResponse(ItemPage itemPage, AccountType accountType, AppUser appUser) {
        return itemResponseCreationServices.stream()
            .filter(service -> accountType.equals(service.getAccountType()))
            .findAny()
            .map(service -> service.toItemDTOResponse(itemPage, appUser))
            .orElseGet(ItemDTOResponse::emptyItemDTOResponse);
    }
}
