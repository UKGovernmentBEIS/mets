package uk.gov.pmrv.api.reporting.domain;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GlobalWarmingPotential {
    CO2(new TreeMap<>(Map.of(0, BigDecimal.ONE, 2026, BigDecimal.ONE))),
    N2O(new TreeMap<>(Map.of(0, BigDecimal.valueOf(298), 2026, BigDecimal.valueOf(265)))),
    PFC_CF4(new TreeMap<>(Map.of(0, BigDecimal.valueOf(7390), 2026, BigDecimal.valueOf(6630)))),
    PFC_C2F6(new TreeMap<>(Map.of(0, BigDecimal.valueOf(12200), 2026, BigDecimal.valueOf(11100))));

    private final NavigableMap<Integer, BigDecimal> valuePerYear;

    public BigDecimal getValue() {
        return valuePerYear.firstEntry().getValue();
    }

    public BigDecimal getValue(Year reportingYear) {
        int year = reportingYear != null ? reportingYear.getValue() : 0;
        Map.Entry<Integer, BigDecimal> entry = valuePerYear.floorEntry(year);
        if (entry == null) {
            throw new IllegalArgumentException("No global warming potential defined for year: " + year);
        }
        return entry.getValue();
    }
}
