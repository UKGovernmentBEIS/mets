package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BatchReissueRequestCreateActionPayload extends RequestCreateActionPayload {

	@Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "payloadType", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = PermitBatchReissueFilters.class, name = "PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpBatchReissueFilters.class, name = "EMP_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD"),
    })
	private BatchReissueFilters filters;
	
	@NotBlank
    private String signatory;
}
