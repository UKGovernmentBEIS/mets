package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.mapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionApplicationSubmittedCustomMapperTest {

    @InjectMocks
    private InstallationOnsiteInspectionApplicationSubmittedCustomMapper mapper;

    @Test
    void toRequestActionDTO(){
        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();
        FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();

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

        Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());

        InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload = InstallationInspectionApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .installationInspection(installationInspection)
                .officialNotice(officialNotice)
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED)
    			.payload(actionPayload)
    			.build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(InstallationInspectionApplicationSubmittedRequestActionPayload.class);

    	InstallationInspectionApplicationSubmittedRequestActionPayload resultPayload = (InstallationInspectionApplicationSubmittedRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }

    @Test
    void toRequestActionDTO_inspectionDetailsHasRegulatorFiles_regulatorFilesShouldBeHiddenFromOperator(){
        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();
        FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();

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

        Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());

        InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload = InstallationInspectionApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                  .inspectionAttachments(Map.of(att1,"att1",att2,"att2",regulatorAtt1,"regulatorAtt1",regulatorAtt2,"regulatorAtt2"))
                .installationInspection(installationInspection)
                .officialNotice(officialNotice)
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED)
    			.payload(actionPayload)
    			.build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

    	InstallationInspectionApplicationSubmittedRequestActionPayload resultPayload = (InstallationInspectionApplicationSubmittedRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getInspectionAttachments()).hasSize(2);
        assertThat(resultPayload.getInspectionAttachments()).doesNotContainKeys(regulatorAtt1,regulatorAtt2);
        assertThat(resultPayload.getInstallationInspection().getDetails().getRegulatorExtraFiles()).isEmpty();
    }

    @Test
    void toRequestActionDTO_inspectionDetailsHasAdditionalInformation_additionalInformationShouldBeHiddenFromOperator(){
        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();
        FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();


        InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(
                    InstallationInspectionDetails
                            .builder()
                            .additionalInformation("test")
                            .build()
                )
                .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());

        InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload = InstallationInspectionApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .installationInspection(installationInspection)
                .officialNotice(officialNotice)
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED)
    			.payload(actionPayload)
    			.build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

    	InstallationInspectionApplicationSubmittedRequestActionPayload resultPayload = (InstallationInspectionApplicationSubmittedRequestActionPayload) result.getPayload();

        assertThat(resultPayload.getInstallationInspection().getDetails().getAdditionalInformation()).isBlank();
    }
}
