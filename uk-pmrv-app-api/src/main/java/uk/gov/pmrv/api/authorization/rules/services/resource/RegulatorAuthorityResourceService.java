package uk.gov.pmrv.api.authorization.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityResourceService {

    private final AuthorityRepository authorityRepository;

    public Map<CompetentAuthorityEnum, Set<String>> findUserScopedRequestTaskTypes(String userId) {
        return authorityRepository
            .findResourceSubTypesRegulatorUserHasScope(userId, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
    }
    
    /**
     * Find regulator users by type subtype and CA
     * @param resourceType
     * @param resourceSubType
     * @param scope
     * @param competentAuthority
     * @return
     */
    public List<String> findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
            ResourceType resourceType, String resourceSubType, Scope scope, CompetentAuthorityEnum competentAuthority){
        return authorityRepository.findRegulatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndCA(
                resourceType, resourceSubType, scope, competentAuthority);
    }
    
    /**
     * Find regulator users by CA
     * @param competentAuthority
     * @return
     */
    public List<String> findUsersByCompetentAuthority(CompetentAuthorityEnum competentAuthority){
        return authorityRepository.findRegulatorUsersByCompetentAuthority(competentAuthority);
    }
    
}
