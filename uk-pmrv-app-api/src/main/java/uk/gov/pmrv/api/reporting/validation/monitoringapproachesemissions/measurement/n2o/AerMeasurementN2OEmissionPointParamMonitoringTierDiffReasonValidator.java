package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerMeasurementN2OEmissionPointParamMonitoringTierDiffReasonValidator implements AerMeasurementN2OEmissionPointEmissionValidator {

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission,
                                       AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        MeasurementN2OEmissionPointEmission measurementN2OEmissionPointEmission =
            (MeasurementN2OEmissionPointEmission) measurementEmissionPointEmission;
        if (!StringUtils.isEmpty(measurementN2OEmissionPointEmission.getId())) {
            String emissionPointId = measurementN2OEmissionPointEmission.getEmissionPoint();
            MeasurementOfN2OMeasuredEmissionsTier measurementEmissionPointEmissionTier =
                measurementN2OEmissionPointEmission.getTier();
            MeasurementOfN2OMeasuredEmissionsTier permitMeasurementN2OMeasuredEmissionsTier =
                aerContainer.getPermitOriginatedData()
                    .getPermitMonitoringApproachMonitoringTiers()
                    .getMeasurementN2OEmissionPointParamMonitoringTiers()
                    .get(measurementN2OEmissionPointEmission.getId());

            boolean areSameMonitoringTiersUsed =
                measurementEmissionPointEmissionTier == permitMeasurementN2OMeasuredEmissionsTier;
            boolean diffReasonExists =
                ObjectUtils.isNotEmpty(measurementN2OEmissionPointEmission.getParameterMonitoringTierDiffReason());

            if (areSameMonitoringTiersUsed && diffReasonExists) {
                violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, emissionPointId));
            }

            if (!areSameMonitoringTiersUsed) {
                if (!diffReasonExists) {
                    violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, emissionPointId));
                } else {
                    ParameterMonitoringTierDiffReason diffReason =
                        measurementN2OEmissionPointEmission.getParameterMonitoringTierDiffReason();
                    List<String> relatedNotifications = diffReason.getRelatedNotifications();
                    if (CollectionUtils.isNotEmpty(relatedNotifications)) {
                        validateDiffReasonNotificationIds(relatedNotifications,
                            aerContainer.getPermitOriginatedData().getPermitNotificationIds(), diffReason.getType(),
                            emissionPointId)
                            .ifPresent(violations::add);
                    }
                }
            }
        }

        return violations;
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
        return new AerViolation(MeasurementOfN2OEmissions.class.getSimpleName(), message, data);
    }
}
