package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReasonType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.AerMeasurementEmissionPointEmissionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator implements AerMeasurementCO2EmissionPointEmissionValidator {

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission,
                                       AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        MeasurementCO2EmissionPointEmission measurementCO2EmissionPointEmission =
            (MeasurementCO2EmissionPointEmission) measurementEmissionPointEmission;
        if (!StringUtils.isEmpty(measurementCO2EmissionPointEmission.getId())) {
            String emissionPointId = measurementCO2EmissionPointEmission.getEmissionPoint();
            MeasurementOfCO2MeasuredEmissionsTier measurementEmissionPointEmissionTier =
                measurementCO2EmissionPointEmission.getTier();
            MeasurementOfCO2MeasuredEmissionsTier permitMeasurementCO2MeasuredEmissionsTier =
                aerContainer.getPermitOriginatedData()
                    .getPermitMonitoringApproachMonitoringTiers()
                    .getMeasurementCO2EmissionPointParamMonitoringTiers()
                    .get(measurementCO2EmissionPointEmission.getId());

            boolean areSameMonitoringTiersUsed =
                measurementEmissionPointEmissionTier == permitMeasurementCO2MeasuredEmissionsTier;
            boolean diffReasonExists =
                ObjectUtils.isNotEmpty(measurementCO2EmissionPointEmission.getParameterMonitoringTierDiffReason());

            if (areSameMonitoringTiersUsed && diffReasonExists) {
                violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, emissionPointId));
            }

            if (!areSameMonitoringTiersUsed) {
                if (!diffReasonExists) {
                    violations.add(buildAerViolation(AerViolation.AerViolationMessage.INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON, emissionPointId));
                } else {
                    ParameterMonitoringTierDiffReason diffReason =
                        measurementCO2EmissionPointEmission.getParameterMonitoringTierDiffReason();
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
        return new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(), message, data);
    }
}
