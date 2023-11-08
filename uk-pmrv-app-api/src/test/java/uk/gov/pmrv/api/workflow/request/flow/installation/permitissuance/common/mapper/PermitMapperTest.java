package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper.PermitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

class PermitMapperTest {

    private final PermitMapper permitMapper = Mappers.getMapper(PermitMapper.class);
    
    @Test
    void toPermitContainer_from_PermitIssuanceRequestPayload_granted() {
    	 TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
         annualEmissionsTargets.put("2024", BigDecimal.valueOf(25000.15));
         
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        PermitIssuanceDeterminateable determination = PermitIssuanceGrantDetermination.builder()
            .type(DeterminationType.GRANTED)
            .activationDate(LocalDate.now().plusDays(10))
            .reason("reason")
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.HSE)
            .permit(permit)
            .determination(determination)
            .build();

        PermitContainer permitContainer = permitMapper.toPermitContainer(permitIssuanceRequestPayload, installationOperatorDetails);

        assertEquals(PermitType.HSE, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertThat(permitContainer.getActivationDate()).isEqualTo(((PermitIssuanceGrantDetermination)determination).getActivationDate());
        assertThat(permitContainer.getAnnualEmissionsTargets()).containsAllEntriesOf(((PermitIssuanceGrantDetermination)determination).getAnnualEmissionsTargets());
    }

    @Test
    void toPermitContainer_fromPermitIssuanceRequestPayload_withInstallationOperatorDetails() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        PermitIssuanceDeterminateable determination = PermitIssuanceRejectDetermination.builder()
            .type(DeterminationType.REJECTED)
            .officialNotice("notice")
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .determination(determination)
            .build();

        PermitContainer permitContainer = permitMapper.toPermitContainer(permitIssuanceRequestPayload, installationOperatorDetails);

        assertEquals(PermitType.GHGE, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertNull(permitContainer.getActivationDate());
        assertThat(permitContainer.getAnnualEmissionsTargets()).isEmpty();
    }

    @Test
    void toPermitContainer_fromPermitIssuanceApplicationReviewRequestTaskPayload_determination_rejected() {
        PermitType permitType = PermitType.GHGE;
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permitType(permitType)
                .permit(permit)
                .installationOperatorDetails(installationOperatorDetails)
                .determination(PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED).build())
                .build();

        PermitContainer permitContainer = permitMapper.toPermitContainer(applicationReviewRequestTaskPayload);

        assertEquals(permitType, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertNull(permitContainer.getActivationDate());
        assertThat(permitContainer.getAnnualEmissionsTargets()).isEmpty();
    }

}
