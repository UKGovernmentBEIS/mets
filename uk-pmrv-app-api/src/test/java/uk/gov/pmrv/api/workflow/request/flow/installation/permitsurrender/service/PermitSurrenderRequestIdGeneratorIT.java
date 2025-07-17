package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderRequestIdGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, PermitSurrenderRequestIdGenerator.class})
class PermitSurrenderRequestIdGeneratorIT extends AbstractContainerBaseTest {

    public static final long TEST_ACCOUNT_ID = 1L;

    @Autowired
    private PermitSurrenderRequestIdGenerator cut;
    @Autowired
    private RequestSequenceRepository repository;

    @Test
    void shouldGenerateRequestId() {
        String requestId = cut.generate(RequestParams.builder()
            .accountId(TEST_ACCOUNT_ID)
            .type(RequestType.PERMIT_SURRENDER)
            .build());

        assertThat(requestId).isEqualTo("AEMS00001-1");
        RequestSequence seq =
            repository.findByBusinessIdentifierAndType(String.valueOf(TEST_ACCOUNT_ID), RequestType.PERMIT_SURRENDER).get();
        assertThat(seq.getVersion()).isZero();
    }

    @Test
    void shouldGenerateRequestIdWhenSequenceAlreadyExists() {

        RequestParams params = RequestParams.builder()
            .accountId(TEST_ACCOUNT_ID)
            .type(RequestType.PERMIT_SURRENDER)
            .build();

        cut.generate(params); // first

        String requestId = cut.generate(params); // second

        assertThat(requestId).isEqualTo("AEMS00001-2");
        RequestSequence seq =
            repository.findByBusinessIdentifierAndType(String.valueOf(TEST_ACCOUNT_ID), RequestType.PERMIT_SURRENDER).get();
        assertThat(seq.getVersion()).isEqualTo(1);
    }

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(RequestType.PERMIT_SURRENDER);
    }
}
