package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitTransferOperatorInfo {

    private String id;

    private String name;
}
