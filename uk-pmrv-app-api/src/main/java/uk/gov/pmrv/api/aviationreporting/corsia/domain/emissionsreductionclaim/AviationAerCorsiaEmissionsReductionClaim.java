package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#emissionsReductionClaimDetails != null)}", message = "aviationAer.corsia.emissionsReductionClaim.exist")
public class AviationAerCorsiaEmissionsReductionClaim {

    @NotNull
    private Boolean exist;

    @Valid
    private AviationAerCorsiaEmissionsReductionClaimDetails emissionsReductionClaimDetails;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        if (emissionsReductionClaimDetails != null) {
            attachments.addAll(emissionsReductionClaimDetails.getCefFiles());
            attachments.addAll(emissionsReductionClaimDetails.getNoDoubleCountingDeclarationFiles());
        }
        return Collections.unmodifiableSet(attachments);
    }

}
