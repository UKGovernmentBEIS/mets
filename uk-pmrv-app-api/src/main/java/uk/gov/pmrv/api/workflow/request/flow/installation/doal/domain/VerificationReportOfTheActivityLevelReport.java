package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationReportOfTheActivityLevelReport {

    @NotNull
    private UUID document;

    @Size(max = 10000)
    @NotBlank
    private String comment;
}
