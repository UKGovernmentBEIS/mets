package uk.gov.pmrv.api.migration.workflow.permitbatchreissue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitBatchReissueSequenceVO {

	private CompetentAuthorityEnum ca;
	private int latestReissueId;
}
