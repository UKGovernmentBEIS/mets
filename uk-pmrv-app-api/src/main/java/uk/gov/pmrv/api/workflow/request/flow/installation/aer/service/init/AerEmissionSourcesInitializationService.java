package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.reporting.domain.Aer;

@Service
public class AerEmissionSourcesInitializationService implements AerSectionInitializationService {

    @Override
    public void initialize(Aer aer, Permit permit) {
        aer.setEmissionSources(permit.getEmissionSources());
    }
}
