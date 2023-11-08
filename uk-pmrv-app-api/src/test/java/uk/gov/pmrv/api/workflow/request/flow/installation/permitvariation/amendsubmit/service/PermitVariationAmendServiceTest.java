package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermitVariationAmendServiceTest {

    private final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample name").build();
    private final Long accountId = 1L;

    @InjectMocks
    private PermitVariationAmendService cut;

    @Mock
    private PermitReviewService permitReviewService;

    @Mock
    private PermitValidatorService permitValidatorService;

    @Test
    void amendPermitVariation() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
                MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder().build()
            )).build())
            .build();

        PermitVariationApplicationAmendsSubmitRequestTaskPayload permitVariationApplicationAmendsSubmitRequestTaskPayload =
            PermitVariationApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewSectionsCompleted(Map.of("entry", true))
                .permitType(PermitType.HSE)
                .permit(permit)
                .reviewGroupDecisions(Map.of(
                    PermitReviewGroup.INSTALLATION_DETAILS, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    PermitReviewGroup.CALCULATION_PFC, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .permitVariationDetailsReviewCompleted(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT)
            .payload(permitVariationApplicationAmendsSubmitRequestTaskPayload)
            .build();

        Permit requestActionPermit = Permit.builder()
            .installationDescription(InstallationDescription.builder().mainActivitiesDesc("mainAct").siteDescription("siteDescription").build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
                MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build()
            )).build())
            .build();

        PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload permitVariationAmendRequestTaskActionPayload =
            PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND_PAYLOAD)
                .permit(requestActionPermit)
                .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
                .permitSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), List.of(true)))
                .permitVariationDetailsCompleted(true)
                .reviewSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), true))
                .permitVariationDetailsAmendCompleted(true)
                .permitVariationDetailsReviewCompleted(false)
                .build();

        cut.amendPermitVariation(permitVariationAmendRequestTaskActionPayload, requestTask);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(PermitVariationApplicationAmendsSubmitRequestTaskPayload.class);

        PermitVariationApplicationAmendsSubmitRequestTaskPayload
            payloadSaved = (PermitVariationApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getPermit().getInstallationDescription().getMainActivitiesDesc())
            .isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermit().getInstallationDescription().getMainActivitiesDesc());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getSiteDescription())
            .isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermit().getInstallationDescription().getSiteDescription());
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName())
            .isEqualTo(permitVariationApplicationAmendsSubmitRequestTaskPayload.getInstallationOperatorDetails().getInstallationName());
        assertThat(payloadSaved.getPermitSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitVariationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitVariationAmendRequestTaskActionPayload.getReviewSectionsCompleted());
        assertThat(payloadSaved.getPermitVariationDetails()).isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermitVariationDetails());
        assertThat(payloadSaved.getPermitVariationDetailsCompleted()).isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermitVariationDetailsCompleted());
        assertThat(payloadSaved.getPermitVariationDetailsAmendCompleted()).isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermitVariationDetailsAmendCompleted());
        assertThat(payloadSaved.getPermitVariationDetailsReviewCompleted()).isEqualTo(permitVariationAmendRequestTaskActionPayload.getPermitVariationDetailsReviewCompleted());
    }

    @Test
    void submitAmendedPermitVariation() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
                    MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build()))
                .build())
            .build();

        Map<UUID, String> permitAttachments = Map.of(
            UUID.randomUUID(), "file1",
            UUID.randomUUID(), "file2"
        );

        PermitVariationApplicationAmendsSubmitRequestTaskPayload permitVariationApplicationAmendsSubmitRequestTaskPayload =
            PermitVariationApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .permit(permit)
                .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
                .permitAttachments(permitAttachments)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true),
                    "amendDetails", List.of(true)
                ))
                .permitVariationDetailsCompleted(true)
                .reviewSectionsCompleted(Map.of("entry", true))
                .permitVariationDetailsReviewCompleted(true)
                .build();

        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder().accountId(accountId).payload(requestPayload).build();

        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT)
            .payload(permitVariationApplicationAmendsSubmitRequestTaskPayload)
            .request(request)
            .build();

        PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload =
            PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true)
                ))
                .build();

        PermitContainer permitContainer = PermitContainer.builder()
            .permit(permit)
            .installationOperatorDetails(installationOperatorDetails)
            .permitAttachments(permitAttachments)
            .build();

        doNothing().when(permitValidatorService).validatePermit(permitContainer);

        cut.submitAmendedPermitVariation(submitApplicationAmendRequestTaskActionPayload, requestTask);

        //verify
        verify(permitReviewService, times(1))
            .cleanUpDeprecatedReviewGroupDecisions(requestPayload, Set.of(MonitoringApproachType.CALCULATION_CO2));

        assertThat(requestTask.getRequest().getPayload()).isInstanceOf(PermitVariationRequestPayload.class);

        PermitVariationRequestPayload
            payloadSaved = (PermitVariationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(payloadSaved.getPermit()).isEqualTo(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermit());
        assertThat(payloadSaved.getPermitAttachments())
            .containsExactlyInAnyOrderEntriesOf(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitAttachments());
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitVariationApplicationAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
        assertThat(payloadSaved.getPermitSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(submitApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
        assertThat(payloadSaved.getPermitVariationDetails()).isEqualTo(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetails());
        assertThat(payloadSaved.getPermitVariationDetailsCompleted()).isEqualTo(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetailsCompleted());
        assertThat(payloadSaved.getPermitVariationDetailsReviewCompleted()).isEqualTo(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetailsReviewCompleted());
    }
    
}

