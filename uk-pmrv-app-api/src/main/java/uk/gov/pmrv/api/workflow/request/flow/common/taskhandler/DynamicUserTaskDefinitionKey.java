package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum DynamicUserTaskDefinitionKey {

    APPLICATION_REVIEW,
    WAIT_FOR_REVIEW,
    APPLICATION_PEER_REVIEW,
    WAIT_FOR_PEER_REVIEW,
    WAIT_FOR_AMENDS,
    APPLICATION_AMENDS_SUBMIT,
    SYSTEM_MESSAGE_NOTIFICATION_TASK,
    RFI_RESPONSE_SUBMIT,
    WAIT_FOR_RFI_RESPONSE,
    RDE_RESPONSE_SUBMIT,
    WAIT_FOR_RDE_RESPONSE,
    MAKE_PAYMENT,
    TRACK_PAYMENT,
    CONFIRM_PAYMENT,
    APPLICATION_VERIFICATION_SUBMIT,
    WAIT_FOR_VERIFICATION,
    ;

    public static Optional<DynamicUserTaskDefinitionKey> fromString(String value) {
    	return Arrays.stream(values())
    	          .filter(key -> key.name().equals(value))
    	          .findFirst();
    }
}
