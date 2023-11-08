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
class AerVerificationProcessSourcesInitializationServiceTest {

    @InjectMocks
    private AerVerificationProcessSourcesInitializationService aerVerificationProcessSourcesInitializationService;

    @Test
    void initialize() {
        final Aer aer = Aer.builder()
                .sourceStreams(SourceStreams.builder()
                        .sourceStreams(List.of(
                                SourceStream.builder()
                                        .reference("ref 1")
                                        .type(SourceStreamType.REFINERIES_HYDROGEN_PRODUCTION)
                                        .description(SourceStreamDescription.GAS_COKE)
                                        .build(),
                                SourceStream.builder()
                                        .reference("ref 2")
                                        .type(SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS)
                                        .description(SourceStreamDescription.BIOMASS)
                                        .build(),
                                SourceStream.builder()
                                        .reference("ref 3")
                                        .type(SourceStreamType.SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY)
                                        .description(SourceStreamDescription.ANTHRACITE)
                                        .build(),
                                SourceStream.builder()
                                    .reference("ref 4")
                                    .type(SourceStreamType.LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B)
                                    .description(SourceStreamDescription.OTHER)
                                    .otherDescriptionName("otherDescriptionName")
                                    .build()
                        ))
                        .build())
                .build();
        final AerVerificationData aerVerificationData = AerVerificationData.builder()
                .opinionStatement(OpinionStatement.builder().build())
                .build();

        aerVerificationProcessSourcesInitializationService.initialize(aerVerificationData, aer);
        assertEquals(2, aerVerificationData.getOpinionStatement().getProcessSources().size());
        assertThat(aerVerificationData.getOpinionStatement().getProcessSources()).containsExactlyInAnyOrder(
            "Gas Coke", "otherDescriptionName"
        );
    }


}
