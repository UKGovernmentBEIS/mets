package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PermitOriginatedCalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerCalculationPfcSourceStreamParamMonitoringTierDiffReasonValidator implements AerCalculationOfPfcSourceStreamEmissionValidator {

    @Override
    public List<AerViolation> validate(PfcSourceStreamEmission pfcSourceStreamEmission,
                                       AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        if (!StringUtils.isEmpty(pfcSourceStreamEmission.getId())) {
            String sourceStreamId = pfcSourceStreamEmission.getSourceStream();
            CalculationPfcParameterMonitoringTier aerSourceStreamParamMonitoringTier =
                pfcSourceStreamEmission.getParameterMonitoringTier();
            PermitOriginatedCalculationPfcParameterMonitoringTier permitSourceStreamParamMonitoringTier =
                aerContainer.getPermitOriginatedData()
                    .getPermitMonitoringApproachMonitoringTiers().getCalculationPfcSourceStreamParamMonitoringTiers().get(pfcSourceStreamEmission.getId());

            boolean areSameSourceStreamParamMonitoringTiersUsed = areTheSame(aerSourceStreamParamMonitoringTier,
                pfcSourceStreamEmission.isMassBalanceApproachUsed(), permitSourceStreamParamMonitoringTier);
            boolean diffReasonExists =
                ObjectUtils.isNotEmpty(pfcSourceStreamEmission.getParameterMonitoringTierDiffReason());

            if (areSameSourceStreamParamMonitoringTiersUsed && diffReasonExists) {
                violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, sourceStreamId));
            }

            if (!areSameSourceStreamParamMonitoringTiersUsed) {
                if (!diffReasonExists) {
                    violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, sourceStreamId));
                } else {
                    ParameterMonitoringTierDiffReason diffReason =
                        pfcSourceStreamEmission.getParameterMonitoringTierDiffReason();
                    List<String> relatedNotifications = diffReason.getRelatedNotifications();
                    if (CollectionUtils.isNotEmpty(relatedNotifications)) {
                        validateDiffReasonNotificationIds(relatedNotifications,
                            aerContainer.getPermitOriginatedData().getPermitNotificationIds(), diffReason.getType(),
                            sourceStreamId)
                            .ifPresent(violations::add);
                    }
                }
            }
        }

        return violations;
    }

    private boolean areTheSame(CalculationPfcParameterMonitoringTier aerSourceStreamParamMonitoringTier,
                               boolean massBalanceApproachUsed,
                               PermitOriginatedCalculationPfcParameterMonitoringTier permitSourceStreamParamMonitoringTier) {
        return aerSourceStreamParamMonitoringTier.getEmissionFactorTier() == permitSourceStreamParamMonitoringTier.getEmissionFactorTier()
            && aerSourceStreamParamMonitoringTier.getActivityDataTier() == permitSourceStreamParamMonitoringTier.getActivityDataTier()
            && massBalanceApproachUsed == permitSourceStreamParamMonitoringTier.isMassBalanceApproachUsed();
    }

    private Optional<AerViolation> validateDiffReasonNotificationIds(List<String> diffReasonNotificationIds,
                                                                     List<String> permitNotificationIds,
                                                                     ParameterMonitoringTierDiffReasonType diffReasonType,
                                                                     String emissionPointId) {
        AerViolation aerViolation = null;

        if (diffReasonType == ParameterMonitoringTierDiffReasonType.OTHER) {
            aerViolation =
                buildAerViolation(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST, emissionPointId);
        } else {
            List<String> diff = new ArrayList<>(diffReasonNotificationIds);
            if (CollectionUtils.isNotEmpty(permitNotificationIds)) {
                diff.removeAll(permitNotificationIds);
            }

            if (!diff.isEmpty()) {
                aerViolation = buildAerViolation(
                    AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS,
                    String.format("%s - %s", emissionPointId, String.join(",", diff)));
            }
        }

        return Optional.ofNullable(aerViolation);
    }

    private AerViolation buildAerViolation(AerViolation.AerViolationMessage message, Object... data) {
        return new AerViolation(CalculationOfPfcEmissions.class.getSimpleName(), message, data);
    }
}
