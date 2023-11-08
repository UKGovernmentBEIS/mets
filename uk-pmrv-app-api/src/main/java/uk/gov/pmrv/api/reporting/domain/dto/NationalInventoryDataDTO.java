package uk.gov.pmrv.api.reporting.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NationalInventoryDataDTO {

    private Set<Sector> sectors;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class Sector {
        private String name;
        private String displayName;
        private Set<Fuel> fuels;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @EqualsAndHashCode
        @Builder
        public static class Fuel {
            private String name;
            private InventoryEmissionCalculationParamsDTO emissionCalculationParameters;
        }
    }
}
