package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpDetailsDTO {

	private String id;
	
	@Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();
	
	@With
	private FileInfoDTO fileDocument;
	
}
