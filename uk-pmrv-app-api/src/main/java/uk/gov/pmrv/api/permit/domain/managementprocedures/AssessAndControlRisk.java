package uk.gov.pmrv.api.permit.domain.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AssessAndControlRisk extends ManagementProceduresDefinition {

    private Set<UUID> riskAssessmentAttachments;

}
