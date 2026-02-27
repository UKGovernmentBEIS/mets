package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BDRS2Bdrs2DataRegulatorReviewAcceptedDecisionDetails extends BDRS2RegulatorReviewDecisionDetails {
}
