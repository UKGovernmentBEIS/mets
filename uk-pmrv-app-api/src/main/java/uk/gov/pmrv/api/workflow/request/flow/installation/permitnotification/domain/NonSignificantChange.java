package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonSignificantChange extends PermitNotification {

    @NotNull
    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<NonSignificantChangeRelatedChangeType> relatedChanges;
}
