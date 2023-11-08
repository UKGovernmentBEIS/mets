package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataRfiable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationVirRequestMetadata extends RequestMetadata implements RequestMetadataReportable, RequestMetadataRfiable {

    @NotNull
    @PastOrPresent
    private Year year;

    @NotBlank
    private String relatedAerRequestId;

    @Builder.Default
    private List<LocalDateTime> rfiResponseDates = new ArrayList<>();
}
