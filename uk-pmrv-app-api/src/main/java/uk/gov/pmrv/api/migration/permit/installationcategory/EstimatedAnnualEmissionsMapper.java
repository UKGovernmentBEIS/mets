package uk.gov.pmrv.api.migration.permit.installationcategory;

import org.apache.commons.lang3.math.NumberUtils;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;

import java.math.BigDecimal;

public class EstimatedAnnualEmissionsMapper {

    static EstimatedAnnualEmissions toEstimatedAnnualEmissions(EtsEstimatedAnnualEmissions etsEstimatedAnnualEmissions) {
        EstimatedAnnualEmissions estimatedAnnualEmissions = new EstimatedAnnualEmissions();
        String estimatedAnnualEmissionStr = etsEstimatedAnnualEmissions.getEstimatedAnnualEmission();
        if (NumberUtils.isParsable(estimatedAnnualEmissionStr)) {
            BigDecimal estimatedAnnualEmission = new BigDecimal(estimatedAnnualEmissionStr);
            estimatedAnnualEmissions.setQuantity(estimatedAnnualEmission);
        }
        return estimatedAnnualEmissions;
    }
}
