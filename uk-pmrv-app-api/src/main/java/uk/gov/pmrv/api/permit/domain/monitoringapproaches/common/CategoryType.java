package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {
    MAJOR("Major"),
    MINOR("Minor"),
    DE_MINIMIS("De-minimis"),
    MARGINAL("Marginal");

    private String description;
}