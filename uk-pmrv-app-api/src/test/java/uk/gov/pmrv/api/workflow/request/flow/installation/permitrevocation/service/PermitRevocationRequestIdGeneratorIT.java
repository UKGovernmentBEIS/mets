package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.PermitRevocationRequestIdGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, PermitRevocationRequestIdGenerator.class})
class PermitRevocationRequestIdGeneratorIT extends AbstractContainerBaseTest {

    public static final long TEST_ACCOUNT_ID = 1L;

    @Autowired
    private PermitRevocationRequestIdGenerator generator;
    @Autowired
    private RequestSequenceRepository repository;

    @Test
    void shouldGenerateRequestId() {
        
        final String requestId = generator.generate(RequestParams.builder()
            .accountId(TEST_ACCOUNT_ID)
            .type(RequestType.PERMIT_REVOCATION)
            .build());

        assertThat(requestId).isEqualTo("AEMR00001-1");

        final RequestSequence seq =
            repository.findByBusinessIdentifierAndType(String.valueOf(TEST_ACCOUNT_ID), RequestType.PERMIT_REVOCATION).get();
        
        assertThat(seq.getVersion()).isZero();
    }

    @Test
    void shouldGenerateRequestIdWhenSequenceAlreadyExists() {

        final RequestParams params = RequestParams.builder()
            .accountId(TEST_ACCOUNT_ID)
            .type(RequestType.PERMIT_REVOCATION)
            .build();

        generator.generate(params); // first

        final String requestId = generator.generate(params); // second

        assertThat(requestId).isEqualTo("AEMR00001-2");

        final RequestSequence seq =
            repository.findByBusinessIdentifierAndType(String.valueOf(TEST_ACCOUNT_ID), RequestType.PERMIT_REVOCATION).get();
        
        assertThat(seq.getVersion()).isEqualTo(1);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.PERMIT_REVOCATION);
    }
}
