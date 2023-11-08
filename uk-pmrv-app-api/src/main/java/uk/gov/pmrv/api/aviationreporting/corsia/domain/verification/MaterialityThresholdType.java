package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaterialityThresholdType {

    THRESHOLD_2_PER_CENT("2%"),
    THRESHOLD_5_PER_CENT("5%")
    ;

    private final String description;
}
