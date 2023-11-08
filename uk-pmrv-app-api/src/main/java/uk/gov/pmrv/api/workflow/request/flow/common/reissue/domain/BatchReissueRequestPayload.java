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
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BatchReissueRequestPayload extends RequestPayload {

	@Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "payloadType", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = PermitBatchReissueFilters.class, name = "PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpBatchReissueFilters.class, name = "EMP_BATCH_REISSUE_REQUEST_PAYLOAD"),
    })
	private BatchReissueFilters filters;
	
	@NotBlank
    private String signatory;
	
	private FileInfoDTO report;
}
