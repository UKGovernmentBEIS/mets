package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationDreUkEtsApplicationSubmitRequestTaskPayload extends AviationDreApplicationRequestTaskPayload {

    private AviationDre dre;

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return dre != null ? Collections.unmodifiableSet(dre.getSupportingDocuments()) : Collections.emptySet();
    }
}
