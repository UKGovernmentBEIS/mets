package uk.gov.pmrv.api.account.installation.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InstallationCategory {

    A_LOW_EMITTER("Category A (Low emitter)"),
    A("Category A"),
    B("Category B"),
    C("Category C"),
    N_A("-");

    private String description;
}