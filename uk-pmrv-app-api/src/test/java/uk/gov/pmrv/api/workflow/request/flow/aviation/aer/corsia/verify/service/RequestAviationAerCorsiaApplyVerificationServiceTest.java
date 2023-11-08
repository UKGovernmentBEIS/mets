package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerCorsiaApplyVerificationServiceTest {

    @InjectMocks
    private RequestAviationAerCorsiaApplyVerificationService service;

    @Test
    void applySaveAction() {
        final AviationAerCorsiaVerificationData verificationData = AviationAerCorsiaVerificationData.builder()
                .emissionsReductionClaimVerification(AviationAerCorsiaEmissionsReductionClaimVerification.builder()
                        .reviewResults("Review results")
                        .build())
                .build();
        final Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("subtask", List.of(true));
        final AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload taskActionPayload =
                AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD)
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .verificationReport(AviationAerCorsiaVerificationReport.builder().build())
                        .build())
                .build();

        service.applySaveAction(taskActionPayload, requestTask);

        assertThat(requestTask.getPayload()).isInstanceOf(AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload.class);
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload resultPayload =
                (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(resultPayload.getVerificationReport().getVerificationData()).isEqualTo(verificationData);
        assertThat(resultPayload.getVerificationSectionsCompleted()).isEqualTo(verificationSectionsCompleted);
    }
}
