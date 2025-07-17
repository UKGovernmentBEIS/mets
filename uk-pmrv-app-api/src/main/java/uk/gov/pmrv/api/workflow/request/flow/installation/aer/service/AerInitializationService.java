package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSectionInitializationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AerInitializationService {

    private final List<AerSectionInitializationService> aerSectionInitializationServices;

    public Aer initializeAer(Permit permit) {
        Aer aer = new Aer();
        aerSectionInitializationServices.forEach(sectionInitializer -> sectionInitializer.initialize(aer, permit));
        return aer;
    }
}
