package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaRequestIdGeneratorTest {

    @InjectMocks
    private AviationDoECorsiaRequestIdGenerator generator;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
        long currentSequence = 1;
        Long accountId = 1L;
        Year year = Year.of(2025);
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .type(RequestType.AVIATION_DOE_CORSIA)
                .requestMetadata(AviationDoECorsiaRequestMetadata.builder().year(year).build())
                .build();

        String businessIdentifierKey = accountId + "-" + year.getValue();

        RequestSequence requestSequence = RequestSequence.builder()
                .id(2L)
                .businessIdentifier(businessIdentifierKey)
                .sequence(currentSequence)
                .type(RequestType.AVIATION_DOE_CORSIA)
                .build();

        when(requestSequenceRepository.findByBusinessIdentifierAndType(businessIdentifierKey, RequestType.AVIATION_DOE_CORSIA))
                .thenReturn(Optional.of(requestSequence));

        final String result = generator.generate(params);

        assertThat(result).isEqualTo("DOECOR" + "00001" + "-" + year + "-" + (currentSequence + 1));
        final ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);
        verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
        final RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
        assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.AVIATION_DOE_CORSIA);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("DOECOR");
    }
}
