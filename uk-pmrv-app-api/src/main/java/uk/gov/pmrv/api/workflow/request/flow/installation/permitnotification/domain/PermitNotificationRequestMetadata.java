package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
public class PermitNotificationRequestMetadata extends RequestMetadata implements RequestMetadataRfiable {

    @Builder.Default
    private List<LocalDateTime> rfiResponseDates = new ArrayList<>();
}
