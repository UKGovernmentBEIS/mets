package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReissueCompletedRequestActionPayload extends RequestActionPayload {

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
