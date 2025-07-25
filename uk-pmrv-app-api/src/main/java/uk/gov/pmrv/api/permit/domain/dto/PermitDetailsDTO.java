package uk.gov.pmrv.api.permit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitDetailsDTO {

	private String id;
	
	private LocalDate activationDate;
	
	@Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();
	
	@With
	private FileInfoDTO fileDocument;
	
}
