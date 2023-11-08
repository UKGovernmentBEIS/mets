package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

class PermitVariationMapperTest {

	private final PermitVariationMapper mapper = Mappers.getMapper(PermitVariationMapper.class);

    @Test
    void toPermitContainer_from_request_payload() {
    	TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.1));
    	
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationRequestPayload permitVariationRequestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.HSE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.determination(PermitVariationGrantDetermination.builder()
    					.type(DeterminationType.GRANTED)
    					.activationDate(LocalDate.now().plusDays(1))
    					.annualEmissionsTargets(annualEmissionsTargets)
    					.build())
    			.build();
    	
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("installationName2").build();
    	
    	PermitContainer result = mapper.toPermitContainer(permitVariationRequestPayload, installationOperatorDetails);
    	
    	assertThat(result).isEqualTo(PermitContainer.builder()
    			.permitType(PermitType.HSE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName2").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.activationDate(LocalDate.now().plusDays(1))
    			.annualEmissionsTargets(annualEmissionsTargets)
    			.build());
    }
    
    @Test
    void toPermitContainer_from_request_payload_for_regulator_led() {
    	TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.1));
    	
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationRequestPayload permitVariationRequestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.HSE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
    					.logChanges("logChanges")
    					.reasonTemplate(PermitVariationReasonTemplate.FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR)
    					.activationDate(LocalDate.now().plusDays(1))
    					.annualEmissionsTargets(annualEmissionsTargets)
    					.build())
    			.build();
    	
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("installationName2").build();
    	
    	PermitContainer result = mapper.toPermitContainer(permitVariationRequestPayload, installationOperatorDetails);
    	
    	assertThat(result).isEqualTo(PermitContainer.builder()
    			.permitType(PermitType.HSE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName2").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.activationDate(LocalDate.now().plusDays(1))
    			.annualEmissionsTargets(annualEmissionsTargets)
    			.build());
    }
    
    @Test
    void toPermitContainer_from_regulator_led_request_payload() {
    	TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.1));
    	
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationRequestPayload permitVariationRequestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
    					.activationDate(LocalDate.now().plusDays(1))
    					.annualEmissionsTargets(annualEmissionsTargets)
    					.build())
    			.build();
    	
    	InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("installationName2").build();
    	
    	PermitContainer result = mapper.toPermitContainer(permitVariationRequestPayload, installationOperatorDetails);
    	
    	assertThat(result).isEqualTo(PermitContainer.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.installationOperatorDetails(InstallationOperatorDetails.builder().installationName("installationName2").build())
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.activationDate(LocalDate.now().plusDays(1))
    			.annualEmissionsTargets(annualEmissionsTargets)
    			.build());
    }
    
    @Test
    void toPermitVariationRequestInfo() {
    	LocalDateTime submissionDate = LocalDateTime.now();
    	LocalDateTime endDate = LocalDateTime.now();
    	PermitVariationRequestMetadata metadata = PermitVariationRequestMetadata.builder()
    			.logChanges("logChanges")
    			.build();
    	Request request = Request.builder()
    			.type(RequestType.PERMIT_VARIATION)
    			.id("requestId")
    			.submissionDate(submissionDate)
    			.endDate(endDate)
    			.metadata(metadata)
    			.build();
    	
    	PermitVariationRequestInfo result = mapper.toPermitVariationRequestInfo(request);
    	
    	assertThat(result).isEqualTo(PermitVariationRequestInfo.builder()
    			.id("requestId")
    			.submissionDate(submissionDate)
    			.endDate(endDate)
    			.metadata(metadata)
    			.build());
    }
    
    @Test
    void toPermitVariationRequestInfo_with_endDate_param() {
    	LocalDateTime submissionDate = LocalDateTime.now();
    	LocalDateTime endDate1 = LocalDateTime.now().minusDays(10);
    	LocalDateTime endDate2 = LocalDateTime.now();
    	PermitVariationRequestMetadata metadata = PermitVariationRequestMetadata.builder()
    			.logChanges("logChanges")
    			.build();
    	Request request = Request.builder()
    			.type(RequestType.PERMIT_VARIATION)
    			.id("requestId")
    			.submissionDate(submissionDate)
    			.endDate(endDate1)
    			.metadata(metadata)
    			.build();
    	
    	PermitVariationRequestInfo result = mapper.toPermitVariationRequestInfo(request, endDate2);
    	
    	assertThat(result).isEqualTo(PermitVariationRequestInfo.builder()
    			.id("requestId")
    			.submissionDate(submissionDate)
    			.endDate(endDate2)
    			.metadata(metadata)
    			.build());
    }
}
