package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueChangesDetails;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmpBatchReissueChangesDetails extends BatchReissueChangesDetails {


    @NotNull
    @Size(min = 1)
    private List<String> changes;
}
