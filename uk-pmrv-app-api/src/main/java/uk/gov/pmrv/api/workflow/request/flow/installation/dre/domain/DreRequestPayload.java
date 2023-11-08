package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class DreRequestPayload extends RequestPayload implements RequestPayloadPayable {

	private Dre dre;
	
	private boolean sectionCompleted;
	
	@Builder.Default
    private Map<UUID, String> dreAttachments = new HashMap<>();
	
	private DecisionNotification decisionNotification;
	
	private RequestPaymentInfo requestPaymentInfo;
	
	private FileInfoDTO officialNotice;
	
	private Year reportingYear;
	
	private AerInitiatorRequest initiatorRequest;

}
