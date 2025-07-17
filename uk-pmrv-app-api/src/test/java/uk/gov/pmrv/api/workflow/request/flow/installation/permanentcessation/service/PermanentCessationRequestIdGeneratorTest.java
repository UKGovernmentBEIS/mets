package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationRequestIdGeneratorTest {

    @InjectMocks
    private PermanentCessationRequestIdGenerator generator;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(1234L)
                .build();

        String requestId = generator.generate(params);

        assertEquals("PC01234-1", requestId);
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(RequestType.PERMANENT_CESSATION);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("PC", prefix);
    }
}
