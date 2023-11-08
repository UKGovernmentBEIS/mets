package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
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
public class ItemUnassignedVerifierService implements ItemUnassignedService {

    private final ItemVerifierRepository itemVerifierRepository;
    private final ItemResponseService itemResponseService;
    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getUnassignedItems(PmrvUser pmrvUser, AccountType accountType, PagingRequest paging) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemVerifierRepository.findItems(
                pmrvUser.getUserId(),
                ItemAssignmentType.UNASSIGNED,
                scopedRequestTaskTypes,
                paging);

        return itemResponseService.toItemDTOResponse(itemPage, accountType, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
