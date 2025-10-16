package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReissueRequestMetadata extends RequestMetadata {

	@NotBlank
	private String batchRequestId;
	
	@NotBlank
    private String submitterId; //user id
    
	@NotBlank
    private String submitter; //full name
	
	@NotBlank
	private String signatory;

	private RequestType batchRequestType;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "batchRequestType", visible = true)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = PermitBatchReissueChangesDetails.class, name = "PERMIT_BATCH_REISSUE"),
		@JsonSubTypes.Type(value = EmpBatchReissueChangesDetails.class, name = "EMP_BATCH_REISSUE"),
	})
	private BatchReissueChangesDetails changesDetails;
}
