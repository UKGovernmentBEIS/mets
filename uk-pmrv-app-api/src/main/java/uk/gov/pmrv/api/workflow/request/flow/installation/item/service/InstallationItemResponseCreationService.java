package uk.gov.pmrv.api.workflow.request.flow.installation.item.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemResponseCreationService;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstallationItemResponseCreationService extends ItemResponseCreationService {

    private final PermitQueryService permitQueryService;

    public InstallationItemResponseCreationService(UserAuthService userAuthService, UserRoleTypeService userRoleTypeService,
                                                   AccountQueryService accountQueryService, PermitQueryService permitQueryService) {
        super(userAuthService, userRoleTypeService, accountQueryService);
        this.permitQueryService = permitQueryService;
    }

    @Override
    public Map<Long, Optional<String>> getAccountPermitReferenceIdMap(ItemPage itemPage) {
        return itemPage.getItems().stream()
            .map(Item::getAccountId)
            .distinct()
            .collect(Collectors.toMap(accId -> accId, permitQueryService::getPermitIdByAccountId));
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INSTALLATION;
    }
}
