package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum NotificationTemplateWorkflowTaskType {

    RFI("Request for Information"),
    RDE("Determination extension request"),
    
    PERMIT_ISSUANCE("Determination"),
    PERMIT_SURRENDER("Determination"),
    PERMIT_NOTIFICATION("Determination"),
    PERMIT_TRANSFER_B("Determination"),
    PERMIT_NOTIFICATION_FOLLOW_UP("Operator Response"),
    PERMIT_VARIATION("Overall decision"),
    NER("New entrant reserve"),
    VIR_SUBMIT("Submission"),
    VIR_RESPOND_TO_REGULATOR_COMMENTS("Follow up Action"),
    AIR_SUBMIT("Submission"),
    AIR_RESPOND_TO_REGULATOR_COMMENTS("Follow up Action"),
    AER("Submission"),

    INSTALLATION_INSPECTION_OPERATOR_RESPOND("Operator Response"),

    BDR("Submission"),

    PAYMENT("Payment"),


    EMP_ISSUANCE_UKETS("EMP determination"),
    EMP_VARIATION_UKETS("EMP variation determination"),
    AVIATION_AER("Submission"),

    EMP_ISSUANCE_CORSIA("CORSIA EMP determination"),
    EMP_VARIATION_CORSIA("CORSIA EMP variation determination"),
    ;
    
    private final String description;
    
    public static NotificationTemplateWorkflowTaskType fromRequestType(String requestType) {
        return Stream.of(values())
                .filter(workflowTaskType -> workflowTaskType.name().equalsIgnoreCase(requestType))
                .findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Request type %s cannot be mapped to notification template workflow task type: ",
								requestType)));
    }
}
