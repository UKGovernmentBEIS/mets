package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport;

import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.CalculationEmissionCategory;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;

@Service
public class AerVerificationCombustionSourcesInitializationService implements AerVerificationReportSectionInitializationService {

    @Override
    public void initialize(AerVerificationData aerVerificationData, Aer aer) {
        final List<String> combustionSourcesDescriptions = aer.getSourceStreams().getSourceStreams().stream()
                .filter(sourceStream -> CalculationEmissionCategory.COMBUSTION.getSourceStreamTypes()
                        .contains(sourceStream.getType()))
                .map(sourceStream -> sourceStream.getDescription() != SourceStreamDescription.OTHER ? 
                        sourceStream.getDescription().getDescription() : 
                        sourceStream.getOtherDescriptionName()
                ).toList();

        aerVerificationData.getOpinionStatement()
                .setCombustionSources(new HashSet<>(combustionSourcesDescriptions));
    }
}
