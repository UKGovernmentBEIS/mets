package uk.gov.pmrv.api.aviationreporting.common.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmpOriginatedData {

	@Builder.Default
    private Map<UUID, String> operatorDetailsAttachments = new HashMap<>();
	
}
