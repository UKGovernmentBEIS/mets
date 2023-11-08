package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerVerificationRegulatedActivitiesInitializationService implements AerVerificationReportSectionInitializationService {

    @Override
    public void initialize(AerVerificationData aerVerificationData, Aer aer) {

        final Set<RegulatedActivityType> regulatedActivityTypes = aer.getRegulatedActivities().getRegulatedActivities()
                .stream().map(RegulatedActivity::getType).collect(Collectors.toSet());
        aerVerificationData.getOpinionStatement().setRegulatedActivities(regulatedActivityTypes);
    }
}
