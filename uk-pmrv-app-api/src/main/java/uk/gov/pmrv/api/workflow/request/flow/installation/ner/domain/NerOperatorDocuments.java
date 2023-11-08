package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NerOperatorDocuments {

    @NotNull
    @Valid
    private NerOperatorDocumentWithComment newEntrantDataReport;

    @NotNull
    @Valid
    private NerOperatorDocumentWithComment verifierOpinionStatement;

    @NotNull
    @Valid
    private NerOperatorDocumentWithComment monitoringMethodologyPlan;

    @JsonIgnore
    public Set<UUID> getDocuments() {

        final Set<UUID> documents = new HashSet<>();
        if (this.getNewEntrantDataReport() != null && this.getNewEntrantDataReport().getDocument() != null) {
            documents.add(this.getNewEntrantDataReport().getDocument());
        }
        if (this.getVerifierOpinionStatement() != null && this.getVerifierOpinionStatement().getDocument() != null) {
            documents.add(this.getVerifierOpinionStatement().getDocument());
        }
        if (this.getMonitoringMethodologyPlan() != null && this.getMonitoringMethodologyPlan().getDocument() != null) {
            documents.add(this.getMonitoringMethodologyPlan().getDocument());
        }
        return documents;
    }
}
