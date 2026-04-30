package uk.gov.pmrv.api;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DBMigrationIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("netz-docker-tests-db")
                    .withUsername("inmemory")
                    .withPassword("inmemory");


    @BeforeAll
    public void setup() throws SQLException, LiquibaseException {
        String datasourceUrl = POSTGRESQL_CONTAINER.getJdbcUrl();
        String datasourceUsername = POSTGRESQL_CONTAINER.getUsername();
        String datasourcePassword = POSTGRESQL_CONTAINER.getPassword();

        Connection connection = DriverManager.getConnection(datasourceUrl, datasourceUsername, datasourcePassword);
        Liquibase liquibase = new Liquibase(
            "db/migration/changelog-master.test.xml",
            new ClassLoaderResourceAccessor(),
            new JdbcConnection(connection)
        );

        liquibase.setChangeLogParameter("report_datasource_name","\"netz-docker-tests-db\"");
        liquibase.setChangeLogParameter("spring-db-user","inmemory");
        liquibase.update( "" );
        connection.close();
    }

    @Test
    public void noRulesWithoutScopePermission() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement("""
                select resource_type, resource_sub_type, role_type, au_rule.scope from au_rule
                where scope is not null
                and not exists(select * from au_resource_scope_permission where au_rule.resource_type=au_resource_scope_permission.resource_type
                and ((au_rule.resource_sub_type is null and au_resource_scope_permission.resource_sub_type is null) or (au_rule.resource_sub_type=au_resource_scope_permission.resource_sub_type))
                and au_rule.role_type=au_resource_scope_permission.role_type
                and au_rule.scope=au_resource_scope_permission.scope) order by scope;
            """);

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }

    @Test
    public void noScopePermissionsWithoutRules() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement(""" 
                select resource_type, resource_sub_type, role_type, au_resource_scope_permission.scope from au_resource_scope_permission
                where not exists(select * from au_rule where au_rule.resource_type=au_resource_scope_permission.resource_type
                and ((au_rule.resource_sub_type is null and au_resource_scope_permission.resource_sub_type is null) or (au_rule.resource_sub_type=au_resource_scope_permission.resource_sub_type))
                and au_rule.role_type=au_resource_scope_permission.role_type
                and au_rule.scope=au_resource_scope_permission.scope)
                order by scope;
                """);

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }


    @Test
    public void noServicesWithoutRules() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement("select id, name from au_service where id not in (select service_id from au_service_rule);");

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }

    @Test
    public void noViewBasedServicesInExecuteScopeForRequestTaskAccessHandler() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement("""
                  select * from au_service s join au_service_rule sr on sr.service_id = s.id join au_rule r on r.id = sr.rule_id
                  where s.name in ('assignTask', 'releaseTask', 'getCandidateAssigneesByTaskType', 'getTaskItemInfoById')
                  and r.resource_type = 'REQUEST_TASK'
                  and r.handler='requestTaskAccessHandler'
                  and r.scope='REQUEST_TASK_EXECUTE';
            """);

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }

    @Test
    public void noServicesInExecuteAndViewScopeAtTheSameTimeForRequestTaskAccessHandler() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement("""
                 SELECT s.name, s.id, r1.id, r1.resource_sub_type, sr1.rule_id, r1.scope, r2.id, r2.resource_sub_type, sr2.rule_id, r2.scope FROM au_service_rule sr1
                 JOIN au_service_rule sr2 ON sr1.service_id = sr2.service_id AND sr1.rule_id != sr2.rule_id
                 JOIN au_rule r1 ON sr1.rule_id = r1.id AND r1.scope='REQUEST_TASK_VIEW'
                 JOIN au_rule r2 ON sr2.rule_id = r2.id AND r2.scope='REQUEST_TASK_EXECUTE'
                 JOIN au_service s ON sr1.service_id = s.id
                 WHERE  r1.handler='requestTaskAccessHandler' AND r2.handler='requestTaskAccessHandler'
                 AND r1.resource_type='REQUEST_TASK' AND r2.resource_type='REQUEST_TASK'
                 AND r1.resource_sub_type = r2.resource_sub_type;
            """);

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }

    @Test
    public void consultantRoleHasAppropriatePermissions() {
        try (Connection conn = getConnection()) {
            PreparedStatement stm = conn.prepareStatement("""
                 with consultant_permissions as (select distinct (rp."permission") 
                                     from au_role r join au_role_permission rp on r.id = rp.role_id where r.code like '%consultant%'),
                 operator_permissions as (select distinct (rp."permission")
                              from au_role r join au_role_permission rp on r.id = rp.role_id where r.code like '%operator%')
                 select permission from operator_permissions 
                       where permission not in (select permission from consultant_permissions) 
                        and permission not in ('PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK', 'PERM_ACCOUNT_USERS_EDIT');
            """);

            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).isFalse();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    POSTGRESQL_CONTAINER.getJdbcUrl(),
                    POSTGRESQL_CONTAINER.getUsername(),
                    POSTGRESQL_CONTAINER.getPassword()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
