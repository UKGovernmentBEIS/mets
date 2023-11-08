package uk.gov.pmrv.api.authorization.rules.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class ResourceScopePermissionRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ResourceScopePermissionRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getResourceType()).isEqualTo(ResourceType.REQUEST_TASK);
        assertThat(result.get().getResourceSubType()).isEqualTo(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name());
        assertThat(result.get().getScope()).isEqualTo(Scope.REQUEST_TASK_EXECUTE);
    }

    @Test
    void findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope_not_found() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_VIEW);

        assertThat(result).isEmpty();
    }

    @Test
    void findByResourceTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.ACCOUNT, null, Scope.EDIT_USER, RoleType.REGULATOR,
            Permission.PERM_ACCOUNT_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndRoleTypeAndScope(
            ResourceType.ACCOUNT,
            RoleType.REGULATOR,
            Scope.EDIT_USER);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getResourceType()).isEqualTo(ResourceType.ACCOUNT);
        assertThat(result.get().getScope()).isEqualTo(Scope.EDIT_USER);
    }

    @Test
    void findByResourceTypeAndRoleTypeAndScope_not_found() {
        createResourceScopePermission(ResourceType.ACCOUNT, null,
            Scope.EDIT_USER, RoleType.REGULATOR, Permission.PERM_ACCOUNT_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndRoleTypeAndScope(
            ResourceType.ACCOUNT,
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);

        boolean result = repo.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isTrue();
    }

    @Test
    void existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope_not_exist() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);

        boolean result = repo.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_VIEW);

        assertThat(result).isFalse();
    }

    @Test
    void findByResourceTypeAndRoleType() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE.name(),
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK);

        Set<String> resourceSubTypes = repo.findByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR).stream()
            .map(ResourceScopePermission::getResourceSubType).collect(
                Collectors.toSet());

        assertThat(resourceSubTypes.size()).isEqualTo(2);
        assertThat(resourceSubTypes).containsAll(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.toString(),
            RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE.toString()));
    }

    private void createResourceScopePermission(
        ResourceType resourceType, String resourceSubType, Scope scope, RoleType roleType, Permission permission) {
        ResourceScopePermission resource = ResourceScopePermission.builder()
            .resourceType(resourceType)
            .resourceSubType(resourceSubType)
            .roleType(roleType)
            .scope(scope)
            .permission(permission)
            .build();
        entityManager.persist(resource);
    }
}
