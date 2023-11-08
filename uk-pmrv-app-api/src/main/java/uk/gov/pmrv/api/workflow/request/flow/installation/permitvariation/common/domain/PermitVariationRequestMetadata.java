package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataRfiable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationRequestMetadata extends RequestMetadata implements RequestMetadataRfiable {

    @Builder.Default
    private List<LocalDateTime> rfiResponseDates = new ArrayList<>();
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String logChanges;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer permitConsolidationNumber;

    private RoleType initiatorRoleType;
}
