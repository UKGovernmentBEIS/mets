package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemByRequestRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRegulatorService implements ItemService {

    private final RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemByRequestRegulatorRepository itemByRequestRegulatorRepository;
    private final RequestService requestService;

    @Override
    public ItemDTOResponse getItemsByRequest(AppUser appUser, String requestId) {
        Request request = requestService.findRequestById(requestId);
        AccountType accountType = request.getType().getAccountType();

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
                .getUserScopedRequestTaskTypesByAccountType(appUser.getUserId(), accountType);

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemByRequestRegulatorRepository.findItemsByRequestId(scopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, accountType, appUser);
    }

    @Override
    public String getRoleType() {
        return RoleTypeConstants.REGULATOR;
    }
}
