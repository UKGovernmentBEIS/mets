package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#entryAccountingForTransfer) == " +
    "(#transferDirection != null)}",
    message = "permit.monitoringapproach.entryAccountingTransfer.exist")
public class TransferCO2 extends Transfer {

    private TransferCO2Direction transferDirection;
}
