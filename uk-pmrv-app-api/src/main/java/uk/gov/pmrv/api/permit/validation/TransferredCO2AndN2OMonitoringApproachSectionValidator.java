package uk.gov.pmrv.api.permit.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSectionWithTransfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.NO_TRANSFERRED_EMISSIONS_SELECTED;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.TRANSFERRED_CO2_AND_N2O_MONITORING_APPROACH_SHOULD_EXIST;

@Component
@RequiredArgsConstructor
public class TransferredCO2AndN2OMonitoringApproachSectionValidator
    implements PermitSectionContextValidator<TransferredCO2AndN2OMonitoringApproach>, PermitContextValidator,
    PermitGrantedContextValidator {

    @Override
    public PermitValidationResult validate(PermitContainer permitContainer) {
        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches();

        if (hasNoTransferMonitoringApproach(permitContainer)
            && !monitoringApproaches.containsKey(MonitoringApproachType.TRANSFERRED_CO2_N2O)) {
            return PermitValidationResult.validPermit();
        }

        return validate(
            (TransferredCO2AndN2OMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.TRANSFERRED_CO2_N2O),
            permitContainer);
    }

    @Override
    public PermitValidationResult validate(TransferredCO2AndN2OMonitoringApproach permitSection,
                                           PermitContainer permitContainer) {
        AtomicReference<List<PermitViolation>> permitViolations = new AtomicReference<>();

        if (ObjectUtils.isEmpty(permitSection)) {
            addValidationError(permitViolations, TRANSFERRED_CO2_AND_N2O_MONITORING_APPROACH_SHOULD_EXIST);
        }

        if (hasNoTransferMonitoringApproach(permitContainer)) {
            addValidationError(permitViolations, NO_TRANSFERRED_EMISSIONS_SELECTED);
        }

        return constructPermitViolations(permitViolations);
    }

    private static void addValidationError(AtomicReference<List<PermitViolation>> permitViolations,
                                           PermitViolation.PermitViolationMessage permitViolationMessage) {
        permitViolations.updateAndGet(violations -> {
            violations = Objects.isNull(violations) ? Lists.newArrayList() : violations;
            violations.add(new PermitViolation(permitViolationMessage));
            return violations;
        });
    }

    private static boolean hasNoTransferMonitoringApproach(PermitContainer permitContainer) {
        return Stream.of(MonitoringApproachType.CALCULATION_CO2, MonitoringApproachType.MEASUREMENT_CO2,
                MonitoringApproachType.MEASUREMENT_N2O)
            .map(monitoringApproachType -> Optional.ofNullable(
                permitContainer.getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(monitoringApproachType)
            ))
            .filter(Optional::isPresent)
            .noneMatch(permitMonitoringApproachSection ->
                ((PermitMonitoringApproachSectionWithTransfer) permitMonitoringApproachSection.get()).isHasTransfer());
    }

    private static PermitValidationResult constructPermitViolations(AtomicReference<List<PermitViolation>> permitViolations) {
        return PermitValidationResult.builder()
            .valid(ObjectUtils.isEmpty(permitViolations.get()))
            .permitViolations(ObjectUtils.isEmpty(permitViolations.get()) ? new ArrayList<>() :
                permitViolations.get())
            .build();
    }
}
