package uk.gov.pmrv.api.migration.emp.common.abbreviations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpAbbreviation {

	private String fldEmitterId;
	private String fldEmitterDisplayId;
	private String abbreviation;
	private String definition;
}
