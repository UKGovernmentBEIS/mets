package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AerVerificationRegulatedActivitiesInitializationServiceTest {

    @InjectMocks
    private AerVerificationRegulatedActivitiesInitializationService aerVerificationRegulatedActivitiesInitializationService;

    @Test
    void initialize() {

        final Aer aer = Aer.builder()
                .regulatedActivities(AerRegulatedActivities
                        .builder()
                        .regulatedActivities(List.of(
                                AerRegulatedActivity.builder()
                                        .type(RegulatedActivityType.COMBUSTION)
                                        .build(),
                                AerRegulatedActivity.builder()
                                        .type(RegulatedActivityType.COKE_PRODUCTION)
                                        .build()
                        ))
                        .build())
                .build();

        final AerVerificationData aerVerificationData = AerVerificationData.builder()
                .opinionStatement(OpinionStatement.builder().build())
                .build();

        aerVerificationRegulatedActivitiesInitializationService.initialize(aerVerificationData, aer);
        assertEquals(2, aerVerificationData.getOpinionStatement().getRegulatedActivities().size());
        assertTrue(aerVerificationData.getOpinionStatement().getRegulatedActivities().contains(RegulatedActivityType.COMBUSTION));
        assertTrue(aerVerificationData.getOpinionStatement().getRegulatedActivities().contains(RegulatedActivityType.COKE_PRODUCTION));
    }
}
