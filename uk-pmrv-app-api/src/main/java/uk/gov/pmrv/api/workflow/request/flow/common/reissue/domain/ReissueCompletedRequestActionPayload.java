package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.BatchReissueChangesDetailsDeserializer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonDeserialize(using = BatchReissueChangesDetailsDeserializer.class)
public class  ReissueCompletedRequestActionPayload extends RequestActionPayload {

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "payloadType", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = PermitBatchReissueChangesDetails.class, name = "PERMIT_REISSUE_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpBatchReissueChangesDetails.class, name = "EMP_REISSUE_COMPLETED_PAYLOAD")
    })
	private BatchReissueChangesDetails changesDetails;

	@NotBlank
	private String submitter; //full name
	
	@NotBlank
    private String signatory;
	
	@NotBlank
    private String signatoryName; //full name
	
	@NotNull
    private FileInfoDTO officialNotice;

    @NotNull
    private FileInfoDTO document; //permit or emp
    
    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(super.getFileDocuments(),
                Map.of(
                    UUID.fromString(officialNotice.getUuid()), officialNotice.getName(),
                    UUID.fromString(document.getUuid()), document.getName()
                )
            )
            .flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
	
}
