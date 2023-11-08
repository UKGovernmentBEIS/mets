package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadCascadable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PermitTransferARequestPayload extends RequestPayload implements RequestPayloadCascadable, RequestPayloadPayable {

    @JsonUnwrapped
    private PermitTransferDetails permitTransferDetails;

    @Builder.Default
    private Map<UUID, String> transferAttachments = new HashMap<>();
    
    private String relatedRequestId;
    
    private RequestPaymentInfo requestPaymentInfo;

    private FileInfoDTO officialNotice;
}
