package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import java.time.LocalDate;

public enum SubInstallationValidityPeriod {

    ALWAYS(null, null),
    UNTIL_12_2026(null, LocalDate.of(2026, 12, 31)),
    FROM_01_2027(LocalDate.of(2027, 1, 1), null);

    private final LocalDate validFrom;
    private final LocalDate validTo;

    SubInstallationValidityPeriod(LocalDate validFrom, LocalDate validTo) {
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public boolean isValid() {
        LocalDate now = LocalDate.now();

        return (validFrom == null || !now.isBefore(validFrom))
                && (validTo == null || !now.isAfter(validTo));
    }

    public boolean isValid(boolean cbamTransitionToggle) {
        if (cbamTransitionToggle) {
            return switch (this) {
                case ALWAYS, FROM_01_2027 -> true;
                case UNTIL_12_2026 -> false;
            };
        }

        return isValid();
    }
}