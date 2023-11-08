package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBRequestIdGenerator;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, PermitTransferBRequestIdGenerator.class})
class PermitTransferBRequestIdGeneratorIT extends AbstractContainerBaseTest {

    @Autowired
    private PermitTransferBRequestIdGenerator generator;

    @Autowired
    private RequestSequenceRepository repository;

    @Test
    void shouldGenerateRequestId() {

        final Long accountId = 1L;
        final String requestId = generator.generate(RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.PERMIT_TRANSFER_B)
            .build());

        assertThat(requestId).isEqualTo("AEMTB00001-1");

        final RequestSequence seq = repository.findByBusinessIdentifierAndType(String.valueOf(accountId), RequestType.PERMIT_TRANSFER_B).get();
        assertThat(seq.getVersion()).isZero();
    }
}
