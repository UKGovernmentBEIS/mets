package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WasteQDRReviewAcceptedDecisionDetails extends WasteQDRRegulatorReviewDecisionDetails{
}
