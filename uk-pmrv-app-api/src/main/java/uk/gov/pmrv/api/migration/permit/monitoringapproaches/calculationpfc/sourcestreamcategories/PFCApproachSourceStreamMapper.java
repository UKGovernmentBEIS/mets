package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.sourcestreamcategories;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationco2.StringToEnumConverter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

import java.math.BigDecimal;

@Log4j2
@UtilityClass
public final class PFCApproachSourceStreamMapper {

    public static PFCSourceStreamCategory constructPFCSourceStreamCategory(EtsPFCSourceStreamCategory etsPFCSourceStreamCategory) {

        return PFCSourceStreamCategory.builder()
                .sourceStream(etsPFCSourceStreamCategory.getSourceStream())
                .emissionSources(etsPFCSourceStreamCategory.getEmissionSources())
                .annualEmittedCO2Tonnes(new BigDecimal(etsPFCSourceStreamCategory.getEstimatedEmission()))
                .categoryType(StringToEnumConverter.sourceStreamCategoryType(etsPFCSourceStreamCategory.getSourceStreamCategory()))
                .calculationMethod(StringToEnumConverter.pfcCalculationMethod(etsPFCSourceStreamCategory.getCalculationMethod()))
                .emissionPoints(etsPFCSourceStreamCategory.getEmissionPoints())
                .build();
    }

    public static PFCActivityData constructPFCActivityData(EtsPFCSourceStreamCategory etsPFCSourceStreamCategory) {

        return PFCActivityData.builder()
                .massBalanceApproachUsed(etsPFCSourceStreamCategory.isMassBalanceApproachUsed())
                .tier(StringToEnumConverter.activityDataTier(etsPFCSourceStreamCategory.getActivityDataTierApplied()))
                .highestRequiredTier(getActivityDataHighestRequestTier(etsPFCSourceStreamCategory))
                .build();
    }

    public static PFCEmissionFactor constructPFCEmissionFactor(EtsPFCSourceStreamCategory etsPFCSourceStreamCategory) {

        return PFCEmissionFactor.builder()
                .tier(StringToEnumConverter.pfcEmissionFactorTier(etsPFCSourceStreamCategory.getEmissionFactorTierApplied()))
                .highestRequiredTier(getEmissionFactorHighestRequiredTier(etsPFCSourceStreamCategory))
                .build();
    }

    private static HighestRequiredTier getActivityDataHighestRequestTier(EtsPFCSourceStreamCategory etsPFCSourceStreamCategory) {
        if (etsPFCSourceStreamCategory.isActivityDataIsMiddleTier() &&
                etsPFCSourceStreamCategory.getTierJustification() == null) {
            return HighestRequiredTier.builder()
                    .isHighestRequiredTier(true)
                    .build();
        } else if (etsPFCSourceStreamCategory.isActivityDataIsMiddleTier() &&
                etsPFCSourceStreamCategory.getTierJustification() != null) {
            return HighestRequiredTier.builder()
                    .isHighestRequiredTier(false)
                    .noHighestRequiredTierJustification(
                            NoHighestRequiredTierJustification.builder()
                                    .isCostUnreasonable(false)
                                    .isTechnicallyInfeasible(true)
                                    .technicalInfeasibilityExplanation(etsPFCSourceStreamCategory.getTierJustification())
                                    .build()
                    ).build();
        } else {
            return HighestRequiredTier.builder().build();
        }
    }

    private static HighestRequiredTier getEmissionFactorHighestRequiredTier(EtsPFCSourceStreamCategory etsPFCSourceStreamCategory) {

        if (etsPFCSourceStreamCategory.isEmissionFactorIsMiddleTier() &&
                etsPFCSourceStreamCategory.getTierJustification() == null) {
            return HighestRequiredTier.builder()
                    .isHighestRequiredTier(true)
                    .build();
        } else if (etsPFCSourceStreamCategory.isActivityDataIsMiddleTier() &&
                etsPFCSourceStreamCategory.getTierJustification() != null) {
            return HighestRequiredTier.builder()
                    .isHighestRequiredTier(false)
                    .noHighestRequiredTierJustification(
                            NoHighestRequiredTierJustification.builder()
                                    .isCostUnreasonable(false)
                                    .isTechnicallyInfeasible(true)
                                    .technicalInfeasibilityExplanation(etsPFCSourceStreamCategory.getTierJustification())
                                    .build()
                    ).build();
        } else {
            return HighestRequiredTier.builder().build();
        }
    }
}
