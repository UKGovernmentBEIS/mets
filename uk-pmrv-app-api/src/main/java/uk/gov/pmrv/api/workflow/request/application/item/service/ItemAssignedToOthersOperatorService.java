package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemOperatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersOperatorService implements ItemAssignedToOthersService {

    private final ItemOperatorRepository itemOperatorRepository;
    private final ItemResponseService itemResponseService;
    private final OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getItemsAssignedToOthers(PmrvUser pmrvUser, AccountType accountType, PagingRequest paging) {
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = operatorAuthorityResourceAdapter
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemOperatorRepository.findItems(
            pmrvUser.getUserId(),
            ItemAssignmentType.OTHERS,
            userScopedRequestTaskTypes,
            paging);

        return itemResponseService.toItemDTOResponse(itemPage, accountType, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }
}
