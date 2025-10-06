package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIAllocationPeriod;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HSETIRequestIdGeneratorTest {

    @InjectMocks
    private HSETIRequestIdGenerator hsetiRequestIdGenerator;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
        long currentSequence = 1;

        RequestParams params = RequestParams.builder()
                .accountId(1L)
                .type(RequestType.HSE_TI)
                .requestMetadata(HSETIRequestMetadata.builder()
                        .type(RequestMetadataType.HSE_TI)
                        .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                        .build())
                .build();

        RequestSequence requestSequence = RequestSequence.builder()
                .id(1L)
                .sequence(currentSequence)
                .type(RequestType.HSE_TI)
                .businessIdentifier("1-21_25")
                .build();

        when(requestSequenceRepository.findByBusinessIdentifierAndType("1-21_25", RequestType.HSE_TI))
                .thenReturn(Optional.of(requestSequence));

        String requestId = hsetiRequestIdGenerator.generate(params);

        assertEquals("HSETI00001-21_25-2", requestId);

       verify(requestSequenceRepository, times(1))
                .findByBusinessIdentifierAndType("1-21_25", RequestType.HSE_TI);
        verify(requestSequenceRepository, times(1)).save(requestSequence);
    }


    @Test
    void getTypes() {
        assertThat(hsetiRequestIdGenerator.getTypes()).containsExactly(RequestType.HSE_TI);
    }

    @Test
    void getPrefix() {
        String prefix = hsetiRequestIdGenerator.getPrefix();

        assertEquals("HSETI", prefix);
    }
}
