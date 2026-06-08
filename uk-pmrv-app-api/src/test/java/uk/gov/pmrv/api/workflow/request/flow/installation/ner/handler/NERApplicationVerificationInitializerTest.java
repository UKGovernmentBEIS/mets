package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERApplicationVerificationInitializerTest {

    @InjectMocks
    private NERApplicationVerificationInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void initializePayload_when_vb_has_not_been_changed_should_not_reset_verification() {
        final long accountId = 1L;
        final Long vbId = 2L;
        final UUID attachmentId = UUID.randomUUID();

        var initialVBDetails = buildVBDetails("old vb details");
        var latestVBDetails = buildVBDetails("latest vb");

        NerRequestPayload requestPayload =
                buildRequestPayload(vbId, initialVBDetails, attachmentId, true);

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(vbId)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(buildInstallationOperatorDetails());

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(vbId))
                .thenReturn(Optional.of(latestVBDetails));

        NERApplicationVerificationSubmitRequestTaskPayload result =
                (NERApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        // 🔹 original payload NOT reset
        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertThat(requestPayload.getVerificationAttachments())
                .isEqualTo(Map.of(attachmentId, "test"));
        assertThat(requestPayload.getVerificationSectionsCompleted())
                .isEqualTo(Map.of("test", List.of(true)));

        // 🔹 result verification report uses EXISTING data
        assertThat(result.getVerificationReport()).isEqualTo(
                NERVerificationReport.builder()
                        .verificationBodyId(vbId)
                        .verificationBodyDetails(latestVBDetails)
                        .verificationData(requestPayload.getVerificationData())
                        .build()
        );

        verify(installationOperatorDetailsQueryService)
                .getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService)
                .getVerificationBodyDetails(vbId);
    }

    @Test
    void initializePayload_when_vb_has_been_changed_should_reset_verification() {
        final long accountId = 1L;
        final Long requestVBId = 1L;
        final Long reportVBId = 2L;
        final UUID attachmentId = UUID.randomUUID();

        var initialVBDetails = buildVBDetails("old vb details");
        var latestVBDetails = buildVBDetails("latest vb");

        NerRequestPayload requestPayload =
                buildRequestPayload(reportVBId, initialVBDetails, attachmentId, true);

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(buildInstallationOperatorDetails());

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
                .thenReturn(Optional.of(latestVBDetails));

        NERApplicationVerificationSubmitRequestTaskPayload result =
                (NERApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        // 🔹 original payload RESET
        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationAttachments()).isEmpty();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();

        // 🔹 result gets EMPTY verification data
        assertThat(result.getVerificationReport()).isEqualTo(
                NERVerificationReport.builder()
                        .verificationBodyId(requestVBId)
                        .verificationBodyDetails(latestVBDetails)
                        .verificationData(NERVerificationData.builder().build())
                        .build()
        );

        verify(installationOperatorDetailsQueryService)
                .getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService)
                .getVerificationBodyDetails(requestVBId);
    }

    @Test
    void initializePayload_when_verification_body_not_found_should_throw_exception() {
        final long accountId = 1L;
        final Long vbId = 2L;

        NerRequestPayload requestPayload =
                buildRequestPayload(vbId, buildVBDetails("old"), UUID.randomUUID(), true);

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(vbId)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(buildInstallationOperatorDetails());

        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(vbId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> initializer.initializePayload(request))
                .isInstanceOf(BusinessException.class);

        verify(installationOperatorDetailsQueryService)
                .getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService)
                .getVerificationBodyDetails(vbId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .isEqualTo(Set.of(RequestTaskType.NER_APPLICATION_VERIFICATION_SUBMIT, RequestTaskType.NER_AMEND_APPLICATION_VERIFICATION_SUBMIT));
    }

    // ----------------- helpers -----------------

    private InstallationOperatorDetails buildInstallationOperatorDetails() {
        AddressDTO address = AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GB")
                .postcode("postcode")
                .build();

        return InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(address)
                        .build())
                .operator("operator")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("123456")
                .operatorDetailsAddress(address)
                .build();
    }

    private VerificationBodyDetails buildVBDetails(String ref) {
        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        return VerificationBodyDetails.builder()
                .verificationBodyEmissionSchemeDTOS(Set.of(verificationBodyEmissionSchemeDTO))
                .build();
    }

    private NerRequestPayload buildRequestPayload(Long vbId,
                                                  VerificationBodyDetails vbDetails,
                                                  UUID attachmentId,
                                                  boolean populateVerificationData) {

        NERVerificationData data =
                populateVerificationData ? NERVerificationData.builder().build() : null;

        return NerRequestPayload.builder()
                .verificationReport(NERVerificationReport.builder()
                        .verificationBodyId(vbId)
                        .verificationBodyDetails(vbDetails)
                        .verificationData(data)
                        .build())
                .verificationAttachments(Map.of(attachmentId, "test"))
                .verificationSectionsCompleted(Map.of("test", List.of(true)))
                .build();
    }
}
