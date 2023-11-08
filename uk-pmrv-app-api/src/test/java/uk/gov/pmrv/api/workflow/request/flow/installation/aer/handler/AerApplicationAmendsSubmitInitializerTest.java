package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.time.Year;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplicationAmendsSubmitInitializerTest {

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @InjectMocks
    private AerApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {
        AerRequestPayload payload = AerRequestPayload.builder()
            .permitOriginatedData(PermitOriginatedData.builder().permitType(PermitType.GHGE).build())
            .aerAttachments(Map.of(UUID.randomUUID(), "test"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "ReviewAttachment"))
            .aer(Aer.builder().additionalDocuments(AdditionalDocuments.builder().build()).build())
            .verifiedAer(Aer.builder().abbreviations(Abbreviations.builder().build()).build())
            .reviewGroupDecisions(Map.of(AerReviewGroup.FALLBACK,
                AerDataReviewDecision.builder()
                    .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .reviewDataType(AerReviewDataType.AER_DATA).details(ChangesRequiredDecisionDetails.builder().build())
                    .build())
            )
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        Request request = Request.builder()
            .payload(payload)
            .metadata(AerRequestMetadata.builder().year(Year.now()).build())
            .accountId(1L)
            .build();

        InstallationOperatorDetails installationOperatorDetails = new InstallationOperatorDetails();
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
            .thenReturn(installationOperatorDetails);

        RequestTaskPayload result = initializer.initializePayload(request);

        assertEquals(AerApplicationAmendsSubmitRequestTaskPayload.class, result.getClass());
        assertEquals(installationOperatorDetails, ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getInstallationOperatorDetails());
        assertEquals(payload.getAer(), ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getAer());
        assertEquals(payload.getReviewSectionsCompleted(), ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getReviewSectionsCompleted());
        assertEquals(payload.getReviewAttachments(), ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getReviewAttachments());
        assertEquals(payload.getAerAttachments(), ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getAerAttachments());
        assertThat(((AerApplicationAmendsSubmitRequestTaskPayload) result).getReviewGroupDecisions())
            .containsExactlyEntriesOf(payload.getReviewGroupDecisions());
        assertEquals(payload.getPermitOriginatedData(), ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getPermitOriginatedData());
        assertEquals(((AerRequestMetadata) request.getMetadata()).getYear(),
            ((AerApplicationAmendsSubmitRequestTaskPayload) result)
                .getReportingYear());
        assertEquals(payload.getPermitOriginatedData().getPermitType(),
            ((AerApplicationAmendsSubmitRequestTaskPayload) result)
            .getPermitType());

    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();
        assertEquals(Collections.singleton(RequestTaskType.AER_APPLICATION_AMENDS_SUBMIT), result);
    }

}