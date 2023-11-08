package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementco2;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;

@UtilityClass
public class StringToEnumConverter {

    public CategoryType categoryType(final String categoryType) {
        if (categoryType == null) return null;
        return switch (categoryType) {
            case "Major" -> CategoryType.MAJOR;
            case "Minor" -> CategoryType.MINOR;
            case "De-minimis" -> CategoryType.DE_MINIMIS;
            case "Marginal" -> CategoryType.MARGINAL;
            default -> null;
        };
    }

    public MeasuredEmissionsSamplingFrequency measuredSamplingFrequency(final String measuredEmissionsSamplingFrequency) {

        if (measuredEmissionsSamplingFrequency == null) return null;
        return switch (measuredEmissionsSamplingFrequency.toLowerCase()) {
            case "continuous" -> MeasuredEmissionsSamplingFrequency.CONTINUOUS;
            case "daily" -> MeasuredEmissionsSamplingFrequency.DAILY;
            case "weekly" -> MeasuredEmissionsSamplingFrequency.WEEKLY;
            case "monthly" -> MeasuredEmissionsSamplingFrequency.MONTHLY;
            case "biannual" -> MeasuredEmissionsSamplingFrequency.BI_ANNUALLY;
            case "annual" -> MeasuredEmissionsSamplingFrequency.ANNUALLY;
            default -> null;
        };
    }

    public MeasurementOfCO2MeasuredEmissionsTier measMeasuredEmissionTier(String tierApplied) {
        if (tierApplied == null) {
            return null;
        }
        return switch (tierApplied.toLowerCase()) {
            case "tier 1" -> MeasurementOfCO2MeasuredEmissionsTier.TIER_1;
            case "tier 2" -> MeasurementOfCO2MeasuredEmissionsTier.TIER_2;
            case "tier 3" -> MeasurementOfCO2MeasuredEmissionsTier.TIER_3;
            case "tier 4" -> MeasurementOfCO2MeasuredEmissionsTier.TIER_4;
            default -> null;
        };
    }
}
