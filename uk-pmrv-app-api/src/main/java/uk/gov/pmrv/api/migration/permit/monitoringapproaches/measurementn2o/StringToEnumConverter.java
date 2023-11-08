package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementn2o;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;

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

    public MeasurementOfN2OMeasuredEmissionsTier n2OMeasuredEmissionTier(String tierApplied) {
        if (tierApplied == null) {
            return null;
        }
        return switch (tierApplied.toLowerCase()) {
            case "tier 1" -> MeasurementOfN2OMeasuredEmissionsTier.TIER_1;
            case "tier 2" -> MeasurementOfN2OMeasuredEmissionsTier.TIER_2;
            case "tier 3" -> MeasurementOfN2OMeasuredEmissionsTier.TIER_3;
            default -> null;
        };
    }

    public MeasurementOfN2OEmissionType n2OEmissionType(String n2OEmissionType) {
        if (n2OEmissionType == null) {
            return null;
        }
        return switch (n2OEmissionType.toLowerCase()) {
            case "abated" -> MeasurementOfN2OEmissionType.ABATED;
            case "unabated" -> MeasurementOfN2OEmissionType.UNABATED;
            default -> null;
        };
    }

    public MeasurementOfN2OMonitoringApproachType n2OMonitoringApproachType(String n2OMonitoringApproachType) {
        if (n2OMonitoringApproachType == null) {
            return null;
        }
        return switch (n2OMonitoringApproachType.toLowerCase()) {
            case "calculation" -> MeasurementOfN2OMonitoringApproachType.CALCULATION;
            case "measurement" -> MeasurementOfN2OMonitoringApproachType.MEASUREMENT;
            default -> null;
        };
    }
}
