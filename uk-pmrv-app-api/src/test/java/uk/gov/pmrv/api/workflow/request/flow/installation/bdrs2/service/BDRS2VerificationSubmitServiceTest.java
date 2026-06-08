package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRS2VerificationSubmitServiceTest {

    @InjectMocks
    private BDRS2VerificationSubmitService service;

    @Test
    void applySaveAction() {

        final BDRS2RequestPayload aerRequestPayload = BDRS2RequestPayload.builder()
                .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                .build();

        final Long accountId = 100L;
        final String requestId = "requestId";
        final Long verificationBodyId = 101L;
        final UUID attachmentId = UUID.randomUUID();

        final Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(aerRequestPayload)
                .verificationBodyId(verificationBodyId)
                .build();

        final BDRS2VerificationData verificationData = BDRS2VerificationData.builder()
                .opinionStatement(BDRS2VerificationOpinionStatement.builder()
                        .opinionStatementFile(attachmentId)
                        .supportingFiles(Set.of(attachmentId))
                        .notes("Test")
                        .build())
                .build();

        final BDRS2ApplicationVerificationSaveRequestTaskActionPayload actionPayload =
                BDRS2ApplicationVerificationSaveRequestTaskActionPayload.builder()
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(Map.of("group", List.of(true)))
                        .build();

        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO2 = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.CORSIA)
                .accreditationReferenceNumber("accreditationRefNumNew2")
                .accreditationName("name2")
                .build();
        BDRS2ApplicationVerificationSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .verificationAttachments(Map.of(attachmentId, "attachment"))
                        .verificationReport(BDRS2VerificationReport.builder()
                                .verificationBodyDetails(VerificationBodyDetails.builder()
                                        .name("nameNew")
                                        .address(AddressDTO.builder()
                                                .city("cityNew")
                                                .country("countryNew")
                                                .line1("lineNew")
                                                .build())
                                        .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO, verificationBodyEmissionSchemeDTO2))
                                        .build())
                                .build())
                        .build();

        RequestTask requestTask =
                RequestTask.builder().request(request).payload(taskPayload).build();

        service.applySaveAction(actionPayload, requestTask);

        BDRS2ApplicationVerificationSubmitRequestTaskPayload payloadSaved =
                (BDRS2ApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(payloadSaved.getVerificationReport().getVerificationData())
                .isEqualTo(verificationData);
        assertThat(payloadSaved.getVerificationSectionsCompleted())
                .isEqualTo(actionPayload.getVerificationSectionsCompleted());
        assertThat(payloadSaved.getVerificationReport().getVerificationBodyDetails())
                .isEqualTo(taskPayload.getVerificationReport().getVerificationBodyDetails());

        assertThat(((BDRS2RequestPayload) request.getPayload()).getVerificationReport())
                .isEqualTo(taskPayload.getVerificationReport());

        assertThat(((BDRS2RequestPayload) request.getPayload()).isVerificationPerformed())
                .isFalse();

        assertThat(((BDRS2RequestPayload) request.getPayload())
                .getVerificationReport()
                .getVerificationBodyId())
                .isEqualTo(verificationBodyId);

        assertThat(((BDRS2RequestPayload) request.getPayload()).getVerificationSectionsCompleted())
                .containsExactlyEntriesOf(actionPayload.getVerificationSectionsCompleted());

        assertThat(((BDRS2RequestPayload) request.getPayload()).getVerificationAttachments())
                .containsExactlyEntriesOf(taskPayload.getVerificationAttachments());
    }
}
