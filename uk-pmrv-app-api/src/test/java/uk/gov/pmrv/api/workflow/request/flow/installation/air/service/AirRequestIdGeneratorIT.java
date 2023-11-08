package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Year;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, AirRequestIdGenerator.class})
class AirRequestIdGeneratorIT extends AbstractContainerBaseTest {

    @InjectMocks
    private AirRequestIdGenerator cut;

    @Mock
    private RequestSequenceRepository repository;

    @Test
    void generate() {
        long currentSequence = 1;
        Long accountId = 1L;
        Year year = Year.of(2023);
        RequestParams params = RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.AIR)
            .requestMetadata(AirRequestMetadata.builder().year(year).build())
            .build();

        String businessIdentifierKey = accountId + "-" + year.getValue();

        RequestSequence requestSequence = RequestSequence.builder()
            .id(2L)
            .businessIdentifier(businessIdentifierKey)
            .sequence(currentSequence)
            .type(RequestType.AIR)
            .build();

        when(repository.findByBusinessIdentifierAndType(businessIdentifierKey, RequestType.AIR))
            .thenReturn(Optional.of(requestSequence));

        final String result = cut.generate(params);

        assertThat(result).isEqualTo("AIR" + "00001" + "-" + year + "-" + (currentSequence + 1));
        final ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);
        verify(repository, times(1)).save(requestSequenceCaptor.capture());
        final RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
        assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(RequestType.AIR);
    }
}
