package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersVerifierService implements ItemAssignedToOthersService {

    private final ItemVerifierRepository itemVerifierRepository;
    private final ItemResponseService itemResponseService;
    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getItemsAssignedToOthers(AppUser appUser, AccountType accountType, PagingRequest paging) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(appUser, accountType);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemVerifierRepository.findItems(
                appUser.getUserId(),
                ItemAssignmentType.OTHERS,
                scopedRequestTaskTypes,
                paging);

        return itemResponseService.toItemDTOResponse(itemPage, accountType, appUser);
    }

    @Override
    public String getRoleType() {
        return RoleTypeConstants.VERIFIER;
    }
}
