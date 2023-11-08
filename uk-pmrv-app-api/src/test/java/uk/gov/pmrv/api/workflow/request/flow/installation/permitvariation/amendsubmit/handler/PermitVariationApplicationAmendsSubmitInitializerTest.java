package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermitVariationApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private PermitVariationApplicationAmendsSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    public void initializePayload() {
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
                        InherentCO2MonitoringApproach.builder().inherentReceivingTransferringInstallations(Collections.emptyList()).build()
                    ))
                    .build())
                .build())
            .permitAttachments(Map.of(attachment, "att"))
            .build();
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(permitContainer.getPermit())
            .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section2", true))
            .build();
        final Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
            .thenReturn(installationOperatorDetails);

        // invoke
        RequestTaskPayload result = initializer.initializePayload(request);
        PermitVariationApplicationAmendsSubmitRequestTaskPayload variationApplicationAmendsRequestTaskPayload =
            (PermitVariationApplicationAmendsSubmitRequestTaskPayload) result;
        assertThat(variationApplicationAmendsRequestTaskPayload.getPermit()).isEqualTo(requestPayload.getPermit());
        assertThat(variationApplicationAmendsRequestTaskPayload.getPermitType()).isEqualTo(requestPayload.getPermitType());
        assertThat(variationApplicationAmendsRequestTaskPayload.getPermitVariationDetails()).isEqualTo(requestPayload.getPermitVariationDetails());
        assertThat(variationApplicationAmendsRequestTaskPayload.getPermitVariationDetailsCompleted()).isEqualTo(requestPayload.getPermitVariationDetailsCompleted());
        assertThat(variationApplicationAmendsRequestTaskPayload.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
        assertThat(variationApplicationAmendsRequestTaskPayload.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
        assertThat(variationApplicationAmendsRequestTaskPayload.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
    }
}
