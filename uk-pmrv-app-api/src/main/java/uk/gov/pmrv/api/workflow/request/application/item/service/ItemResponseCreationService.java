package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.transform.ItemMapper;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class ItemResponseCreationService implements ItemAccountTypeResponseCreationService {

    private final UserAuthService userAuthService;
    private final UserRoleTypeService userRoleTypeService;
    private final AccountQueryService accountQueryService;
    private static final ItemMapper ITEM_MAPPER = Mappers.getMapper(ItemMapper.class);

    public abstract Map<Long, Optional<String>> getAccountPermitReferenceIdMap(ItemPage itemPage);

    @Override
    public ItemDTOResponse toItemDTOResponse(ItemPage itemPage, PmrvUser pmrvUser) {
        //get user info from keycloak for the task assignee ids
        Map<String, UserInfoDTO> users = getUserInfoForItemAssignees(pmrvUser, itemPage);
        //get accounts for operator or regulator
        Map<Long, ItemAccountDTO> accounts = getAccounts(itemPage);
        Map<Long, Optional<String>> accountPermitReferenceIdMap = getAccountPermitReferenceIdMap(itemPage);

        List<ItemDTO> itemDTOs = itemPage.getItems().stream().map(i -> {
            UserInfoDTO taskAssignee = i.getTaskAssigneeId() != null
                ? users.get(i.getTaskAssigneeId())
                : null;
            RoleType taskAssigneeType = getRoleTypeForItemAssignee(i.getTaskAssigneeId());
            ItemAccountDTO account = accounts.get(i.getAccountId());
            final String permitReferenceId = accountPermitReferenceIdMap.get(i.getAccountId()).orElse(null);
            return ITEM_MAPPER.itemToItemDTO(i,
                taskAssignee,
                taskAssigneeType,
                account,
                permitReferenceId);
        }).collect(Collectors.toList());

        return ItemDTOResponse.builder()
            .items(itemDTOs)
            .totalItems(itemPage.getTotalItems())
            .build();
    }

    private Map<String, UserInfoDTO> getUserInfoForItemAssignees(PmrvUser pmrvUser, ItemPage itemPage) {
        Set<String> userIds = itemPage.getItems().stream()
            .map(Item::getTaskAssigneeId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(userIds))
            return Collections.emptyMap();

        //if the assignee of all items is the pmrvUser
        if (userIds.size() == 1 && userIds.contains(pmrvUser.getUserId()))
            return Map.of(pmrvUser.getUserId(),
                new UserInfoDTO(pmrvUser.getFirstName(), pmrvUser.getLastName()));

        return userAuthService.getUsers(new ArrayList<>(userIds)).stream()
            .collect(Collectors.toMap(
                UserInfo::getId,
                u -> new UserInfoDTO(u.getFirstName(), u.getLastName())));
    }

    private Map<Long, ItemAccountDTO> getAccounts(ItemPage itemPage) {
        List<Long> accountIds = itemPage.getItems()
            .stream().map(Item::getAccountId)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(accountIds))
            return Collections.emptyMap();

        return accountQueryService.getAccountsInfoByIds(accountIds).stream()
            .map(ITEM_MAPPER::accountToItemAccountDTO)
            .collect(Collectors.toMap(ItemAccountDTO::getAccountId, a -> a));
    }

    private RoleType getRoleTypeForItemAssignee(String assignee) {
        return assignee != null ? userRoleTypeService.getUserRoleTypeByUserId(assignee).getRoleType() : null;
    }
}
