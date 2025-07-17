package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationDoECorsiaRequestPayload extends RequestPayload implements RequestPayloadPayable {

    private AviationDoECorsia doe;

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;

    private Year reportingYear;

    private AerInitiatorRequest initiatorRequest;

    private Boolean sectionCompleted;

    private RequestPaymentInfo requestPaymentInfo;

    @Builder.Default
    private Map<UUID, String> doeAttachments = new HashMap<>();
}
