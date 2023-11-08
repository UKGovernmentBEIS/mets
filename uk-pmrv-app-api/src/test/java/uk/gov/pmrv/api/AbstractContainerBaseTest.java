package uk.gov.pmrv.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    static final PostgreSQLContainer<?> POSTGRESQL_MIGRATION_CONTAINER;
	static final PostgreSQLContainer<?> POSTGRESQL_REPORTS_EA_CONTAINER;
	static final PostgreSQLContainer<?> POSTGRESQL_REPORTS_SEPA_CONTAINER;
	static final PostgreSQLContainer<?> POSTGRESQL_REPORTS_NIEA_CONTAINER;
	static final PostgreSQLContainer<?> POSTGRESQL_REPORTS_NRW_CONTAINER;
	static final PostgreSQLContainer<?> POSTGRESQL_REPORTS_OPRED_CONTAINER;

    static {
		POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres")
			.withDatabaseName("pmrv-docker-tests-db")
			.withPassword("inmemory")
			.withUsername("inmemory")
			.withExposedPorts(5444)
			.withStartupTimeout(Duration.ofMinutes(2));


		POSTGRESQL_MIGRATION_CONTAINER = new PostgreSQLContainer<>("postgres")
                .withDatabaseName("pmrv-docker-migration-tests-db")
                .withPassword("inmemory")
                .withUsername("inmemory")
                .withExposedPorts(5445)
			.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_REPORTS_NRW_CONTAINER = new PostgreSQLContainer<>("postgres")
				.withDatabaseName("pmrv-docker-report-nrw-tests-db")
				.withPassword("inmemory")
				.withUsername("inmemory")
				.withExposedPorts(5446)
				.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_REPORTS_EA_CONTAINER = new PostgreSQLContainer<>("postgres")
				.withDatabaseName("pmrv-docker-report-ea-tests-db")
				.withPassword("inmemory")
				.withUsername("inmemory")
				.withExposedPorts(5447)
				.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_REPORTS_SEPA_CONTAINER = new PostgreSQLContainer<>("postgres")
				.withDatabaseName("pmrv-docker-report-sepa-tests-db")
				.withPassword("inmemory")
				.withUsername("inmemory")
				.withExposedPorts(5448)
				.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_REPORTS_NIEA_CONTAINER = new PostgreSQLContainer<>("postgres")
				.withDatabaseName("pmrv-docker-report-niea-tests-db")
				.withPassword("inmemory")
				.withUsername("inmemory")
				.withExposedPorts(5449)
				.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_REPORTS_OPRED_CONTAINER = new PostgreSQLContainer<>("postgres")
				.withDatabaseName("pmrv-docker-report-opred-tests-db")
				.withPassword("inmemory")
				.withUsername("inmemory")
				.withExposedPorts(5450)
				.withStartupTimeout(Duration.ofMinutes(2));

		POSTGRESQL_CONTAINER.start();
    	POSTGRESQL_MIGRATION_CONTAINER.start();
		POSTGRESQL_REPORTS_NRW_CONTAINER.start();
		POSTGRESQL_REPORTS_EA_CONTAINER.start();
		POSTGRESQL_REPORTS_SEPA_CONTAINER.start();
		POSTGRESQL_REPORTS_NIEA_CONTAINER.start();
		POSTGRESQL_REPORTS_OPRED_CONTAINER.start();

	}
    
    @DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
		registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
		
		registry.add("migration-datasource.url", POSTGRESQL_MIGRATION_CONTAINER::getJdbcUrl);
        registry.add("migration-datasource.password", POSTGRESQL_MIGRATION_CONTAINER::getPassword);
        registry.add("migration-datasource.username", POSTGRESQL_MIGRATION_CONTAINER::getUsername);

		registry.add("report-datasource-ea.url", POSTGRESQL_REPORTS_EA_CONTAINER::getJdbcUrl);
		registry.add("report-datasource-ea.username", POSTGRESQL_REPORTS_EA_CONTAINER::getPassword);
		registry.add("report-datasource-ea.password", POSTGRESQL_REPORTS_EA_CONTAINER::getUsername);

		registry.add("report-datasource-nrw.url", POSTGRESQL_REPORTS_NRW_CONTAINER::getJdbcUrl);
		registry.add("report-datasource-nrw.username", POSTGRESQL_REPORTS_NRW_CONTAINER::getPassword);
		registry.add("report-datasource-nrw.password", POSTGRESQL_REPORTS_NRW_CONTAINER::getUsername);

		registry.add("report-datasource-sepa.url", POSTGRESQL_REPORTS_SEPA_CONTAINER::getJdbcUrl);
		registry.add("report-datasource-sepa.username", POSTGRESQL_REPORTS_SEPA_CONTAINER::getPassword);
		registry.add("report-datasource-sepa.password", POSTGRESQL_REPORTS_SEPA_CONTAINER::getUsername);

		registry.add("report-datasource-niea.url", POSTGRESQL_REPORTS_NIEA_CONTAINER::getJdbcUrl);
		registry.add("report-datasource-niea.username", POSTGRESQL_REPORTS_NIEA_CONTAINER::getPassword);
		registry.add("report-datasource-niea.password", POSTGRESQL_REPORTS_NIEA_CONTAINER::getUsername);

		registry.add("report-datasource-opred.url", POSTGRESQL_REPORTS_OPRED_CONTAINER::getJdbcUrl);
		registry.add("report-datasource-opred.username", POSTGRESQL_REPORTS_OPRED_CONTAINER::getPassword);
		registry.add("report-datasource-opred.password", POSTGRESQL_REPORTS_OPRED_CONTAINER::getUsername);
	}
}
