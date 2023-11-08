package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.MeasurementInstrumentOwnerType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetailsConfirmation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler.PermitTransferBApplicationPeerReviewInitializer;

@ExtendWith(MockitoExtension.class)
class PermitTransferBApplicationPeerReviewInitializerTest {

    @InjectMocks
    private PermitTransferBApplicationPeerReviewInitializer handler;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void initializePayload() {

        final UUID attachment = UUID.randomUUID();
        final Long accountId = 1L;

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("Account name")
            .siteName("siteName")
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
            .operatorType(LegalEntityType.LIMITED_COMPANY)
            .companyReferenceNumber("408812")
            .operatorDetailsAddress(AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GR")
                .postcode("postcode")
                .build())
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(false).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                    .monitoringApproaches(Map.of(
                        MonitoringApproachType.INHERENT_CO2,
                        InherentCO2MonitoringApproach.builder().inherentReceivingTransferringInstallations(List.of(
                            InherentReceivingTransferringInstallation.builder()
                                .inherentCO2Direction(InherentCO2Direction.EXPORTED_TO_ETS_INSTALLATION)
                                .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                                .inherentReceivingTransferringInstallationDetailsType(InherentReceivingTransferringInstallationEmitter.builder()
                                    .installationEmitter(InstallationEmitter.builder()
                                        .email("test@test.com")
                                        .emitterId("emitter")
                                        .build())
                                    .build())
                                .measurementInstrumentOwnerTypes(Set.of(
                                    MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION,
                                    MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION)
                                )
                                .totalEmissions(BigDecimal.valueOf(200.32))
                                .build()
                        )).build()
                    ))
                    .build())
                .build())
            .permitAttachments(Map.of(attachment, "att"))
            .build();
        final PermitTransferBRequestPayload requestPayload = PermitTransferBRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(permitContainer.getPermit())
            .permitTransferDetailsConfirmation(PermitTransferDetailsConfirmation.builder().transferAccepted(true).build())
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section2", true))
            .build();
        final Request request = Request.builder().accountId(accountId).payload(requestPayload).build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);

        // invoke
        RequestTaskPayload result = handler.initializePayload(request);
        PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
            (PermitTransferBApplicationReviewRequestTaskPayload) result;
        assertThat(taskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(taskPayload.getPermit()).isEqualTo(requestPayload.getPermit());
        assertThat(taskPayload.getPermitType()).isEqualTo(requestPayload.getPermitType());
        assertThat(taskPayload.getPermitTransferDetailsConfirmation()).isEqualTo(requestPayload.getPermitTransferDetailsConfirmation());
        assertThat(taskPayload.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
    }
}
