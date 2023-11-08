package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2ODirection;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#entryAccountingForTransfer) == " +
    "(#transferDirection != null)}",
    message = "permit.monitoringapproach.entryAccountingTransfer.exist")
public class TransferN2O extends Transfer {
    private TransferN2ODirection transferDirection;
}
