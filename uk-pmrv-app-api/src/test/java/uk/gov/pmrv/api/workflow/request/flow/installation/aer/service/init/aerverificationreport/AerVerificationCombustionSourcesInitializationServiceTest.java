package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AerVerificationCombustionSourcesInitializationServiceTest {

    @InjectMocks
    private AerVerificationCombustionSourcesInitializationService aerVerificationCombustionSourcesInitializationService;

    @Test
    void initialize() {
        final Aer aer = Aer.builder()
                .sourceStreams(SourceStreams.builder()
                        .sourceStreams(List.of(
                                SourceStream.builder()
                                        .reference("ref 1")
                                        .type(SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS)
                                        .description(SourceStreamDescription.GAS_COKE)
                                        .build(),
                                SourceStream.builder()
                                        .reference("ref 2")
                                        .type(SourceStreamType.CERAMICS_CARBON_INPUTS_METHOD_A)
                                        .description(SourceStreamDescription.BIOMASS)
                                        .build(),
                                SourceStream.builder()
                                        .reference("ref 3")
                                        .type(SourceStreamType.CERAMICS_SCRUBBING)
                                        .description(SourceStreamDescription.ANTHRACITE)
                                        .build(),
                                SourceStream.builder()
                                        .reference("ref 4")
                                        .type(SourceStreamType.COMBUSTION_FLARES)
                                        .description(SourceStreamDescription.OTHER)
                                        .otherDescriptionName("otherDescriptionName")
                                        .build()
                        ))
                        .build())
                .build();

        final AerVerificationData aerVerificationData = AerVerificationData.builder()
                .opinionStatement(OpinionStatement.builder().build())
                .build();
        aerVerificationCombustionSourcesInitializationService.initialize(aerVerificationData, aer);
        assertEquals(2, aerVerificationData.getOpinionStatement().getCombustionSources().size());
        assertThat(aerVerificationData.getOpinionStatement().getCombustionSources()).containsExactlyInAnyOrder(
            "Gas Coke", "otherDescriptionName"
        );
    }

}
