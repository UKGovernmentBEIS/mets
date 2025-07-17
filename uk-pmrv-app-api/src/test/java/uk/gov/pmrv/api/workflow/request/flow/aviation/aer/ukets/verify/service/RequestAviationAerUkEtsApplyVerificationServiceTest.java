package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;

import java.util.List;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class RequestAviationAerUkEtsApplyVerificationServiceTest {

    @InjectMocks
    private RequestAviationAerUkEtsApplyVerificationService service;

    @Test
    void applySaveAction() {

        final Long accountId = 100L;
        final String requestId = "requestId";
        final Long verificationBodyId = 101L;


        final AviationAerUkEtsVerificationData verificationData = AviationAerUkEtsVerificationData.builder()
                .emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
                        .reviewResults("Review results")
                        .build())
                .build();

        final Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("subtask", List.of(true));

        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD).build();

        final Request request = Request.builder()
                    .id(requestId)
                    .accountId(accountId)
                    .payload(aerRequestPayload)
                    .verificationBodyId(verificationBodyId)
                    .build();

        final AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload taskActionPayload =
                AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION_PAYLOAD)
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .build();

        final AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload taskPayload = AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .verificationReport(AviationAerUkEtsVerificationReport.builder().build())
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        service.applySaveAction(taskActionPayload, requestTask);

        Assertions.assertThat(requestTask.getPayload()).isInstanceOf(AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload.class);
        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload resultPayload =
                (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();
        Assertions.assertThat(resultPayload.getVerificationReport().getVerificationData()).isEqualTo(verificationData);
        Assertions.assertThat(resultPayload.getVerificationSectionsCompleted()).isEqualTo(verificationSectionsCompleted);

        Assertions.assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).getVerificationReport())
                    .isEqualTo(taskPayload.getVerificationReport());

        Assertions.assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVerificationPerformed()).isFalse();

        Assertions.assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).getVerificationReport().getVerificationBodyId())
                    .isEqualTo(verificationBodyId);

        Assertions.assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).getVerificationSectionsCompleted())
                    .containsExactlyEntriesOf(taskActionPayload.getVerificationSectionsCompleted());
    }
}