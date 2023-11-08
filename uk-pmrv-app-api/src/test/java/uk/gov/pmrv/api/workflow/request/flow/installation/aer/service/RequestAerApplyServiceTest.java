package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringPlanDeviation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestAerApplyServiceTest {

    @InjectMocks
    private RequestAerApplyService service;

    @Test
    void applySaveAction_ghge() {
        AerApplicationSubmitRequestTaskPayload aerApplicationSubmitRequestTaskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .permitType(PermitType.GHGE)
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .verificationPerformed(true)
                .aerSectionsCompleted(new HashMap<>())
                .build();

        RequestTask requestTask = RequestTask.builder().payload(aerApplicationSubmitRequestTaskPayload).build();

        AerSaveApplicationRequestTaskActionPayload aerSaveApplicationRequestTaskActionPayload =
                AerSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .aer(Aer.builder()
                                .additionalDocuments(AdditionalDocuments.builder().exist(true).build())
                                .aerMonitoringPlanDeviation(AerMonitoringPlanDeviation.builder()
                                        .existChangesNotCoveredInApprovedVariations(Boolean.TRUE)
                                        .details("Something I forgot to add")
                                        .build())
                                .build())
                        .aerSectionsCompleted(Map.of(AdditionalDocuments.class.getName(), List.of(true)))
                        .build();

        // Invoke
        service.applySaveAction(aerSaveApplicationRequestTaskActionPayload, requestTask);

        // Verify
        AerApplicationSubmitRequestTaskPayload payloadSaved = (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().isExist()).isTrue();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().getDocuments()).isNull();
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName()).isEqualTo("instName");
        assertThat(payloadSaved.getAerSectionsCompleted()).containsExactly(Map.entry(AdditionalDocuments.class.getName(), List.of(true)));
        assertThat(payloadSaved.isVerificationPerformed()).isFalse();
    }

    @Test
    void applySaveAction_hse() {
        AerApplicationSubmitRequestTaskPayload aerApplicationSubmitRequestTaskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .permitType(PermitType.HSE)
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .verificationPerformed(true)
                .aerSectionsCompleted(new HashMap<>())
                .build();

        RequestTask requestTask = RequestTask.builder().payload(aerApplicationSubmitRequestTaskPayload).build();

        AerSaveApplicationRequestTaskActionPayload aerSaveApplicationRequestTaskActionPayload =
                AerSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .aer(Aer.builder()
                                .additionalDocuments(AdditionalDocuments.builder().exist(true).build())
                                .aerMonitoringPlanDeviation(AerMonitoringPlanDeviation.builder()
                                        .existChangesNotCoveredInApprovedVariations(Boolean.TRUE)
                                        .details("Something I forgot to add")
                                        .build())
                                .build())
                        .aerSectionsCompleted(Map.of(AdditionalDocuments.class.getName(), List.of(true)))
                        .build();

        // Invoke
        service.applySaveAction(aerSaveApplicationRequestTaskActionPayload, requestTask);

        // Verify
        AerApplicationSubmitRequestTaskPayload payloadSaved = (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().isExist()).isTrue();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().getDocuments()).isNull();
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName()).isEqualTo("instName");
        assertThat(payloadSaved.getAerSectionsCompleted()).containsExactly(Map.entry(AdditionalDocuments.class.getName(), List.of(true)));
        assertThat(payloadSaved.isVerificationPerformed()).isFalse();
    }
}
