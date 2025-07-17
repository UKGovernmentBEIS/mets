package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;

import java.time.Year;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#year != null) && (T(java.time.Year).parse(#year).getValue() >= 2021) && (T(java.time.Year).parse(#year).getValue() <= T(java.time.Year).now().getValue())}", message = "installationAuditRequestCreateActionPayload.year")
public class InstallationAuditRequestCreateActionPayload extends RequestCreateActionPayload {

    private Year year;
}
