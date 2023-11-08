package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;
import java.util.HashSet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#documentsExist == (#documents?.size() gt 0)}", message = "permitsurrender.documents.exist")
public class PermitSurrender {

    @NotNull
    @PastOrPresent
    private LocalDate stopDate;
    
    @NotBlank
    @Size(max=10000)
    private String justification;
    
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean documentsExist;
    
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents = new HashSet<>();
    
    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(documents);
    }
    
}
