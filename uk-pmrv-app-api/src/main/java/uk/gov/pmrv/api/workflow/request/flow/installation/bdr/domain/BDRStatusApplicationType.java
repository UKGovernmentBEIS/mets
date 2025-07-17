package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BDRStatusApplicationType {

    NONE("NONE"),
    HSE("HSE"),
    USE("USE");

    private final String description;
}
