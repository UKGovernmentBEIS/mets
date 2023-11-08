package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthority;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DoalAcceptedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private DoalAcceptedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.DOAL_ACCEPTED);
    }

    @Test
    void constructParams() {
        final Year reportingYear = Year.of(2022);
        final Doal doal = Doal.builder()
                .determination(DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .build())
                .build();
        final DoalGrantAuthorityResponse authorityResponse = DoalGrantAuthorityResponse.builder()
                .type(DoalAuthorityResponseType.VALID)
                .build();

        final DoalRequestPayload payload = DoalRequestPayload.builder()
                .reportingYear(reportingYear)
                .doal(doal)
                .doalAuthority(DoalAuthority.builder()
                        .authorityResponse(authorityResponse)
                        .build())
                .build();
        final String requestId = "1";

        assertThat(provider.constructParams(payload, requestId))
                .isEqualTo(Map.of(
                        "reportingYear", reportingYear,
                        "doal", doal,
                        "authorityResponse", authorityResponse
                ));
    }
}
