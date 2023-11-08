package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class RejectDetermination extends Determination {

    @Size(max = 10000)
    @NotBlank
    private String officialNotice;
}