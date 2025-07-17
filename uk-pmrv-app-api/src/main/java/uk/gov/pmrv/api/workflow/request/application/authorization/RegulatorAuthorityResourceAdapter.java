package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityResourceAdapter {
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public Map<CompetentAuthorityEnum, Set<RequestTaskType>> getUserScopedRequestTaskTypesByAccountType(String userId, AccountType accountType){
        Map<CompetentAuthorityEnum, Set<String>> requestTaskTypes = regulatorAuthorityResourceService.findUserScopedRequestTaskTypes(userId);
        return requestTaskTypes.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue()
                        .stream()
                        .map(RequestTaskType::valueOf)
                        .filter(requestTaskType -> accountType.equals(requestTaskType.getRequestType().getAccountType()))
                        .collect(Collectors.toSet())
                )
            );
    }
}
