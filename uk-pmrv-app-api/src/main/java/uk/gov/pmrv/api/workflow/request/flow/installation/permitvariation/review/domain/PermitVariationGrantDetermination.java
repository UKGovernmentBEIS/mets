package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationGrantDetermination extends GrantDetermination implements PermitVariationDeterminateable {
    
	@Size(max = 10000)
	@NotBlank
    private String logChanges;
}
