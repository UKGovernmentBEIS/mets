package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReasonType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingWithdrawal;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WithdrawWithholdingOfAllowancesDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private WithdrawWithholdingOfAllowancesDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWN);
    }

    @Test
    void constructParams() {
        final WithholdingOfAllowancesRequestPayload payload = WithholdingOfAllowancesRequestPayload.builder()
            .payloadType(RequestPayloadType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PAYLOAD)
            .withholdingWithdrawal(
                WithholdingWithdrawal.builder()
                    .reason("reason")
                    .build()
            )
            .withholdingOfAllowances(
                WithholdingOfAllowances.builder()
                    .reasonType(WithholdingOfAllowancesReasonType.OTHER)
                    .year(2023)
                    .otherReason("other reason")
                    .build()
            )
            .build();
        String requestId = "1";

        assertThat(provider.constructParams(payload, requestId))
            .isEqualTo(
                Map.of(
                    "reasonType", payload.getWithholdingOfAllowances().getReasonType(),
                    "year", payload.getWithholdingOfAllowances().getYear(),
                    "otherReason", payload.getWithholdingOfAllowances().getOtherReason(),
                    "withdrawWithholdingReason", payload.getWithholdingWithdrawal().getReason()
                )
            );
    }

}