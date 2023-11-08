package uk.gov.pmrv.api.reporting.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AerValidationResult {

    private boolean valid;

    @Builder.Default
    private List<AerViolation> aerViolations = new ArrayList<>();

    public static AerValidationResult validAer() {
        return AerValidationResult.builder().valid(true).build();
    }

    public static AerValidationResult invalidAer(List<AerViolation> aerViolations) {
        return AerValidationResult.builder().valid(false).aerViolations(aerViolations).build();
    }
}
