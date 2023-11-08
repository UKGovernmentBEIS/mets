package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestExpirationType {

    APPLICATION_REVIEW("applicationReview"),
    RFI("rfi"),
    RDE("rde"),
    FOLLOW_UP_RESPONSE("followUpResponse"),
    PAYMENT("payment"),
    AER("aer"),
    VIR("vir"),
    AIR("air"),

    AVIATION_AER("aviationAer"),
    AVIATION_VIR("aviationVir"),
    ;
    
    private final String code;
}
