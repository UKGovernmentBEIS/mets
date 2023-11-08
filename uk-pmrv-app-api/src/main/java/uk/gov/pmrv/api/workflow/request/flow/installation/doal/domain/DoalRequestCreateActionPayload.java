package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#year != null) && (T(java.time.Year).parse(#year).getValue() >= 2020) && (T(java.time.Year).parse(#year).getValue() <= 2030)}", message = "doalRequestCreateActionPayload.year")
public class DoalRequestCreateActionPayload extends RequestCreateActionPayload {

    private Year year;
}
