package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerCalculationSourceStreamParamMonitoringTierDiffReasonValidator implements AerCalculationSourceStreamEmissionValidator {

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        if(!StringUtils.isEmpty(sourceStreamEmission.getId())) {
            String sourceStreamId = sourceStreamEmission.getSourceStream();
            List<CalculationParameterMonitoringTier> aerSourceStreamParamMonitoringTiers = sourceStreamEmission.getParameterMonitoringTiers();
            List<CalculationParameterMonitoringTier> permitSourceStreamParamMonitoringTiers = aerContainer.getPermitOriginatedData()
                .getPermitMonitoringApproachMonitoringTiers()
                .getCalculationSourceStreamParamMonitoringTiers()
                .get(sourceStreamEmission.getId());

            boolean areSameSourceStreamParamMonitoringTiersUsed =
                CollectionUtils.isEqualCollection(aerSourceStreamParamMonitoringTiers, permitSourceStreamParamMonitoringTiers);
            boolean diffReasonExists = ObjectUtils.isNotEmpty(sourceStreamEmission.getParameterMonitoringTierDiffReason());

            if(areSameSourceStreamParamMonitoringTiersUsed && diffReasonExists) {
                violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, sourceStreamId));
            }

            if(!areSameSourceStreamParamMonitoringTiersUsed) {
                if (!diffReasonExists) {
                    violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, sourceStreamId));
                } else {
                    ParameterMonitoringTierDiffReason diffReason = sourceStreamEmission.getParameterMonitoringTierDiffReason();
                    List<String> relatedNotifications = diffReason.getRelatedNotifications();
                    if(CollectionUtils.isNotEmpty(relatedNotifications)) {
                        validateDiffReasonNotificationIds(relatedNotifications, aerContainer.getPermitOriginatedData().getPermitNotificationIds(), diffReason.getType(), sourceStreamId)
                            .ifPresent(violations::add);
                    }
                }
            }
        }

        return violations;
    }

    private Optional<AerViolation> validateDiffReasonNotificationIds(List<String> diffReasonNotificationIds, List<String> permitNotificationIds,
                                                                     ParameterMonitoringTierDiffReasonType diffReasonType,
                                                                     String sourceStreamId) {
        AerViolation aerViolation = null;

        if(diffReasonType == ParameterMonitoringTierDiffReasonType.OTHER) {
            aerViolation = buildAerViolation(AerViolation.AerViolationMessage.PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST, sourceStreamId);
        } else {
            List<String> diff = new ArrayList<>(diffReasonNotificationIds);
            if(CollectionUtils.isNotEmpty(permitNotificationIds)) {
                diff.removeAll(permitNotificationIds);
            }

            if(!diff.isEmpty()) {
                aerViolation = buildAerViolation(
                    AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS,
                    String.format("%s - %s", sourceStreamId, String.join(",", diff)));
            }
        }

        return Optional.ofNullable(aerViolation);
    }

    private AerViolation buildAerViolation(AerViolation.AerViolationMessage message, Object... data) {
        return new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), message, data);
    }
}
