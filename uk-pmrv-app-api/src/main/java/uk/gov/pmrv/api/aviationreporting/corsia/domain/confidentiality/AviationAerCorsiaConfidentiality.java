package uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#totalEmissionsPublished) == (#totalEmissionsExplanation != null)}", message = "aviationAer.corsia.confidentiality.totalEmissionsExplanation")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#aggregatedStatePairDataPublished) == (#aggregatedStatePairDataExplanation != null)}", message = "aviationAer.corsia.confidentiality.aggregatedStatePairDataExplanation")
public class AviationAerCorsiaConfidentiality {

    @NotNull
    private Boolean totalEmissionsPublished;

    @Size(max = 10000)
    private String totalEmissionsExplanation;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> totalEmissionsDocuments = new HashSet<>();

    @NotNull
    private Boolean aggregatedStatePairDataPublished;

    @Size(max = 10000)
    private String aggregatedStatePairDataExplanation;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> aggregatedStatePairDataDocuments = new HashSet<>();

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {

        final Set<UUID> attachments = new HashSet<>();
        attachments.addAll(totalEmissionsDocuments);
        attachments.addAll(aggregatedStatePairDataDocuments);

        return Collections.unmodifiableSet(attachments);
    }
}
