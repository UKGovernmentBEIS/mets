package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality.AviationAerCorsiaConfidentiality;

import java.util.ArrayList;
import java.util.List;

@Service
public class AviationAerCorsiaConfidentialitySectionValidator implements AviationAerCorsiaContextValidator {


    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaContainer aerContainer) {

        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final AviationAerCorsiaConfidentiality confidentiality = aerContainer.getAer().getConfidentiality();

        if (Boolean.FALSE.equals(confidentiality.getTotalEmissionsPublished())
                && !confidentiality.getTotalEmissionsDocuments().isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerCorsiaConfidentiality.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_DOCUMENTS));
        }

        if (Boolean.FALSE.equals(confidentiality.getAggregatedStatePairDataPublished())
                && !confidentiality.getAggregatedStatePairDataDocuments().isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerCorsiaConfidentiality.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_AGGREGATED_STATE_PAIR_DOCUMENTS));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }
}
