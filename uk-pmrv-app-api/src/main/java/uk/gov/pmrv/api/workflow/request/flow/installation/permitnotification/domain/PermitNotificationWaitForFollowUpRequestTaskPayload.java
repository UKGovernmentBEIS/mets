package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationWaitForFollowUpRequestTaskPayload extends RequestTaskPayload {

    private String followUpRequest;
    
    private LocalDate followUpResponseExpirationDate;
}
