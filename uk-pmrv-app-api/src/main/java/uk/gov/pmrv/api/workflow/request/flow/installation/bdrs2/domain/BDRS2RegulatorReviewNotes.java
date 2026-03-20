package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BDRS2RegulatorReviewNotes {

    // Visible to the operator
    private String operatorNotes;

    // Not visible to the operator
    private String internalNotes;
}
