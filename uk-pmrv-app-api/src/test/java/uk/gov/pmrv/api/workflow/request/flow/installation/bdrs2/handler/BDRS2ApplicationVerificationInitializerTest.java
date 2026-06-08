package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationVerificationInitializerTest {

    @InjectMocks
    private BDRS2ApplicationVerificationInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void initializePayload_when_vb_has_not_been_changed_initialize_the_payload_without_resetting_verification() {
        final long accountId = 1L;
        Long requestVBId = 2L;
        final UUID verificationAttachment = UUID.randomUUID();

        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        VerificationBodyDetails verificationBodyDetails = VerificationBodyDetails.builder()
                .name("vb_name")
                .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO))
                .build();

        BDRS2VerificationReport verificationReport = BDRS2VerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetails)
                .verificationData(BDRS2VerificationData.builder().build())
                .build();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachment, "test");

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                .bdrs2(BDRS2.builder().build())
                .verificationReport(verificationReport)
                .verificationAttachments(verificationAttachments)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .metadata(BDRS2RequestMetadata.builder().build())
                .build();

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .operator("le")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .build();

        VerificationBodyEmissionSchemeDTO latestVerificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .verificationBodyEmissionSchemeDTOS(Set.of(latestVerificationBodyEmissionSchemeDTO))
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
                .thenReturn(Optional.of(latestVerificationBodyDetails));

        BDRS2ApplicationVerificationSubmitRequestTaskPayload result = (BDRS2ApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertThat(requestPayload.getVerificationAttachments()).isEqualTo(verificationAttachments);
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEqualTo(verificationSectionsCompleted);
        assertEquals(RequestTaskPayloadType.BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(BDRS2VerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(latestVerificationBodyDetails)
                .verificationData(requestPayload.getVerificationReport().getVerificationData())
                .build());

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void initializePayload_when_vb_has_been_changed_initialize_the_payload_and_reset_verification() {
        final long accountId = 1L;
        Long requestVBId = 1L;
        Long reportVBId = 2L;
        final UUID verificationAttachment = UUID.randomUUID();

        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        BDRS2VerificationReport verificationReport = BDRS2VerificationReport.builder()
                .verificationBodyId(reportVBId)
                .verificationBodyDetails(VerificationBodyDetails.builder()
                        .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO))
                        .build())
                .verificationData(BDRS2VerificationData.builder().build())
                .build();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachment, "test");

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                .bdrs2(BDRS2.builder().build())
                .verificationReport(verificationReport)
                .verificationAttachments(verificationAttachments)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .metadata(BDRS2RequestMetadata.builder().build())
                .build();

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .operator("le")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .build();

        VerificationBodyEmissionSchemeDTO latestVerificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .verificationBodyEmissionSchemeDTOS(Set.of(latestVerificationBodyEmissionSchemeDTO))
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
                .thenReturn(Optional.of(latestVerificationBodyDetails));

        BDRS2ApplicationVerificationSubmitRequestTaskPayload result = (BDRS2ApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationAttachments()).isEmpty();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();
        assertEquals(RequestTaskPayloadType.BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(BDRS2VerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(latestVerificationBodyDetails)
                .verificationData(BDRS2VerificationData.builder().build())
                .build());

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void getRequestTaskTypes() {
        Assertions.assertEquals(initializer.getRequestTaskTypes(),
                Set.of(RequestTaskType.BDRS2_APPLICATION_VERIFICATION_SUBMIT, RequestTaskType.BDRS2_AMEND_APPLICATION_VERIFICATION_SUBMIT));
    }
}
