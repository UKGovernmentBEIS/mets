package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BDRBdrDataRegulatorReviewAcceptedDecisionDetails extends BDRRegulatorReviewDecisionDetails {
}
