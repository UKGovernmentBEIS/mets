package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler.InstallationOnsiteInspectionOperatorRespondInitializer;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionOperatorRespondInitializerTest {

    @InjectMocks
    private InstallationOnsiteInspectionOperatorRespondInitializer handler;

    @Test
    void initializePayload() {
        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .details(InstallationInspectionDetails
                        .builder()
                        .date(LocalDate.now())
                        .build())
                .build();

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD);
        assertThat(result).isInstanceOf(InstallationInspectionOperatorRespondRequestTaskPayload.class);
        assertThat(result).isEqualTo(InstallationInspectionOperatorRespondRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD).installationInspection(installationInspection).build());
    }

    @Test
    void initializePayload_inspectionDetailsHasRegulatorFiles_regulatorFilesShouldBeHiddenFromOperatorThroughInspectionAttachments() {

        UUID att1 = UUID.randomUUID();
        UUID att2 = UUID.randomUUID();

        UUID regulatorAtt1 = UUID.randomUUID();
        UUID regulatorAtt2 = UUID.randomUUID();

        InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(
                    InstallationInspectionDetails
                            .builder()
                            .files(Set.of(att1,att2))
                            .regulatorExtraFiles(Set.of(regulatorAtt1,regulatorAtt2))
                            .build()
                )
                .build();

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .inspectionAttachments(Map.of(att1,"att1",att2,"att2",regulatorAtt1,"regulatorAtt1",regulatorAtt2,"regulatorAtt2"))
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getAttachments()).hasSize(2);
        assertThat(result.getAttachments()).doesNotContainKeys(regulatorAtt1,regulatorAtt2);
    }

    @Test
    void initializePayload_inspectionDetailsHasRegulatorFiles_regulatorFilesShouldBeEmptyInNewTaskPayload() {

        UUID att1 = UUID.randomUUID();
        UUID att2 = UUID.randomUUID();

        UUID regulatorAtt1 = UUID.randomUUID();
        UUID regulatorAtt2 = UUID.randomUUID();

        InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(
                    InstallationInspectionDetails
                            .builder()
                            .files(Set.of(att1,att2))
                            .regulatorExtraFiles(Set.of(regulatorAtt1,regulatorAtt2))
                            .build()
                )
                .build();

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .inspectionAttachments(Map.of(att1,"att1",att2,"att2",regulatorAtt1,"regulatorAtt1",regulatorAtt2,"regulatorAtt2"))
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        InstallationInspectionOperatorRespondRequestTaskPayload result = (InstallationInspectionOperatorRespondRequestTaskPayload) handler.initializePayload(request);

        assertThat(result.getInstallationInspection().getDetails().getRegulatorExtraFiles()).isEmpty();
    }

    @Test
    void initializePayload_inspectionDetailsHasAdditionalInformation_additionalInformationShouldBeEmptyInNewTaskPayload() {

        UUID att1 = UUID.randomUUID();
        UUID att2 = UUID.randomUUID();

        UUID regulatorAtt1 = UUID.randomUUID();
        UUID regulatorAtt2 = UUID.randomUUID();

        InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(
                    InstallationInspectionDetails
                            .builder()
                            .files(Set.of(att1,att2))
                            .additionalInformation("test")
                            .regulatorExtraFiles(Set.of(regulatorAtt1,regulatorAtt2))
                            .build()
                )
                .build();

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .inspectionAttachments(Map.of(att1,"att1",att2,"att2",regulatorAtt1,"regulatorAtt1",regulatorAtt2,"regulatorAtt2"))
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        InstallationInspectionOperatorRespondRequestTaskPayload result = (InstallationInspectionOperatorRespondRequestTaskPayload) handler.initializePayload(request);

        assertThat(result.getInstallationInspection().getDetails().getAdditionalInformation()).isBlank();
    }
}
