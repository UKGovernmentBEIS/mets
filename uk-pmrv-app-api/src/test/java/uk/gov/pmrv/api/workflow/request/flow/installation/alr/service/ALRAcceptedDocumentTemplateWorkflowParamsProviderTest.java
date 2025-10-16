package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ALRAcceptedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private ALRAcceptedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType())
                .isEqualTo(DocumentTemplateGenerationContextActionType.ALR_ACCEPTED);
    }

    @Test
    void constructParams() {
        final Year reportingYear = Year.of(2023);

        final ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome = ALRApplicationRegulatorReviewOutcome.builder()
                .build();

        final ALRGrantAuthorityResponse authorityResponse = ALRGrantAuthorityResponse.builder()
                .type(DoalAuthorityResponseType.VALID)
                .build();

        final ALRApplicationAuthorityReviewOutcome authorityReviewOutcome = ALRApplicationAuthorityReviewOutcome.builder()
                .authorityResponse(authorityResponse)
                .build();

        final ALRRequestPayload payload = ALRRequestPayload.builder()
                .reportingYear(reportingYear)
                .regulatorReviewOutcome(regulatorReviewOutcome)
                .authorityReviewOutcome(authorityReviewOutcome)
                .build();

        final String requestId = "ALR-001";

        assertThat(provider.constructParams(payload, requestId))
                .isEqualTo(Map.of(
                        "reportingYear", reportingYear,
                        "alr", regulatorReviewOutcome,
                        "authorityResponse", authorityResponse
                ));
    }
}