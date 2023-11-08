package uk.gov.pmrv.api.workflow.request.flow.aviation.item.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemResponseCreationService;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AviationItemResponseCreationService extends ItemResponseCreationService {

    private final EmissionsMonitoringPlanQueryService empQueryService;

    public AviationItemResponseCreationService(UserAuthService userAuthService, UserRoleTypeService userRoleTypeService,
                                               AccountQueryService accountQueryService, EmissionsMonitoringPlanQueryService empQueryService) {
        super(userAuthService, userRoleTypeService, accountQueryService);
        this.empQueryService = empQueryService;
    }

    @Override
    public Map<Long, Optional<String>> getAccountPermitReferenceIdMap(ItemPage itemPage) {
        return itemPage.getItems().stream()
            .map(Item::getAccountId)
            .distinct()
            .collect(Collectors.toMap(accId -> accId, empQueryService::getEmpIdByAccountId));
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.AVIATION;
    }
}
