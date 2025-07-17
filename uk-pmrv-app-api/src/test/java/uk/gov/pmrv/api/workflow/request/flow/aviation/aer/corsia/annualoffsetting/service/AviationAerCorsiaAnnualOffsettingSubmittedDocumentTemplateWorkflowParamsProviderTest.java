package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
	private AviationAerCorsiaAnnualOffsettingSubmittedDocumentTemplateWorkflowParamsProvider provider;

    @Test
	void getContextActionType() {
		assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED);
	}

	@Test
	void constructParams() {

		final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        final Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();
        aviationAerCorsiaAnnualOffsettingSectionsCompleted.put("aviationAerCorsiaAnnualOffsetting",false);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = AviationAerCorsiaAnnualOffsetting
                .builder()
                .calculatedAnnualOffsetting(6)
                .schemeYear(Year.of(2023))
                .sectorGrowth(2.9)
                .totalChapter(1)
                .build();

        AviationAerCorsiaAnnualOffsettingRequestPayload aviationAerCorsiaAnnualOffsettingRequestPayload = AviationAerCorsiaAnnualOffsettingRequestPayload
                .builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PAYLOAD)
                .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(aviationAerCorsiaAnnualOffsettingSectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

		String requestId = "1";

		Map<String, Object> result = provider.constructParams(aviationAerCorsiaAnnualOffsettingRequestPayload, requestId);

		Map<String, Object> expectedParams = new HashMap<>();
        expectedParams.put("schemeYear", aviationAerCorsiaAnnualOffsetting.getSchemeYear().toString());
        expectedParams.put("calculatedAnnualOffsettingRequirements",aviationAerCorsiaAnnualOffsetting.getCalculatedAnnualOffsetting());
        expectedParams.put("schemeYearPlusOne", aviationAerCorsiaAnnualOffsetting.getSchemeYear().plusYears(1));
        expectedParams.put("annualOffsettingRequirements", aviationAerCorsiaAnnualOffsetting.getTotalChapter());
        expectedParams.put("sectorGrowth", aviationAerCorsiaAnnualOffsetting.getSectorGrowth());

		assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedParams);
	}
}
