package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemByRequestVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemVerifierService implements ItemService {

    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemByRequestVerifierRepository itemByRequestVerifierRepository;
    private final RequestService requestService;

    @Override
    public ItemDTOResponse getItemsByRequest(PmrvUser pmrvUser, String requestId) {
        Request request = requestService.findRequestById(requestId);
        AccountType accountType = request.getType().getAccountType();

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = verifierAuthorityResourceAdapter
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemByRequestVerifierRepository.findItemsByRequestId(scopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, accountType, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
