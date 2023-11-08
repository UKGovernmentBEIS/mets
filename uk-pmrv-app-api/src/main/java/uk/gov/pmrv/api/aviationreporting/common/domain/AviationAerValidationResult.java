package uk.gov.pmrv.api.aviationreporting.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerValidationResult {

    private boolean valid;

    @Builder.Default
    private List<AviationAerViolation> aerViolations = new ArrayList<>();

    public static AviationAerValidationResult validAviationAer() {
        return AviationAerValidationResult.builder().valid(true).build();
    }

    public static AviationAerValidationResult invalidAviationAer(List<AviationAerViolation> aerViolations) {
        return AviationAerValidationResult.builder().valid(false).aerViolations(aerViolations).build();
    }
}
