package uk.gov.pmrv.api.authorization.rules.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Repository
public interface ResourceScopePermissionRepository extends JpaRepository<ResourceScopePermission, Long> {

    @Transactional(readOnly = true)
    Optional<ResourceScopePermission> findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
            ResourceType resourceType, String resourceSubType, RoleType roleType, Scope scope);
    
    @Transactional(readOnly = true)
    Optional<ResourceScopePermission> findByResourceTypeAndRoleTypeAndScope(
            ResourceType resourceType, RoleType roleType, Scope scope);
    
    @Transactional(readOnly = true)
	boolean existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType resourceType, String resourceSubType,
			RoleType roleType, Scope scope);

    @Transactional(readOnly = true)
    Set<ResourceScopePermission> findByResourceTypeAndRoleType(ResourceType resourceType, RoleType roleType);
    
}
