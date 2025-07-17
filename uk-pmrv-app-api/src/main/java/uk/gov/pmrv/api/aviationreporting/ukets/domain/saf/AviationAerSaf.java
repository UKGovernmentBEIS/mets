package uk.gov.pmrv.api.aviationreporting.ukets.domain.saf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#safDetails != null)}", message = "aviationAer.saf.exist")
public class AviationAerSaf {

    @NotNull
    private Boolean exist;

    @Valid
    private AviationAerSafDetails safDetails;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        if (safDetails != null && !ObjectUtils.isEmpty(safDetails.getAttachmentIds())) {
            attachments.addAll(safDetails.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }

}
