package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestMetadata;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalRequestIdGeneratorTest {

    @InjectMocks
    private DoalRequestIdGenerator generator;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
        final long currentSequence = 1;
        final Long accountId = 1L;
        final Year year = Year.of(2023);
        final RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .type(RequestType.DOAL)
                .requestMetadata(DoalRequestMetadata.builder().year(year).build())
                .build();

        final String businessIdentifierKey = accountId + "-" + year.getValue();

        final RequestSequence requestSequence = RequestSequence.builder()
                .id(2L)
                .businessIdentifier(businessIdentifierKey)
                .sequence(currentSequence)
                .type(RequestType.DOAL)
                .build();

        when(requestSequenceRepository.findByBusinessIdentifierAndType(businessIdentifierKey, RequestType.DOAL))
                .thenReturn(Optional.of(requestSequence));

        final String result = generator.generate(params);

        assertThat(result).isEqualTo("DOAL" + "00001" + "-" + year + "-" + (currentSequence + 1));
        final ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);
        verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
        final RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
        assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.DOAL);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("DOAL");
    }
}
