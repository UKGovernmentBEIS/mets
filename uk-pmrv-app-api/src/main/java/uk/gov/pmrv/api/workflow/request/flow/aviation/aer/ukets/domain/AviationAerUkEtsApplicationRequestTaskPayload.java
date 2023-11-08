package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsApplicationRequestTaskPayload extends AviationAerApplicationRequestTaskPayload {

    private AviationAerUkEts aer;

    @Override
    public Map<UUID, String> getAttachments() {
        return getAerAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        Set<UUID> attachments = new HashSet<>(super.getReferencedAttachmentIds());
        if (getAer() != null && !ObjectUtils.isEmpty(getAer().getAerSectionAttachmentIds())) {
            attachments.addAll(getAer().getAerSectionAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
