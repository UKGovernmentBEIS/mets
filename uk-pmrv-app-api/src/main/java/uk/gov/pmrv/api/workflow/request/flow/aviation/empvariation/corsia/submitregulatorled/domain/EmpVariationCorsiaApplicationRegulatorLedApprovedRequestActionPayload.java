package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload 
    extends EmpVariationCorsiaApplicationSubmittedRequestActionPayload {

	@Valid
	@NotNull
	private EmissionsMonitoringPlanCorsiaContainer originalEmpContainer;
	
	@Builder.Default
    private Map<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
	
	@Valid
	@NotNull
	private String reasonRegulatorLed;	
	
	@Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();
    
    @NotNull
    private FileInfoDTO officialNotice;

    @NotNull
    private FileInfoDTO empDocument;
    
    @Override
    @JsonIgnore
    public Map<UUID, String> getFileDocuments() {
        
        return Stream.of(super.getFileDocuments(),
                Map.of(
                    UUID.fromString(officialNotice.getUuid()), officialNotice.getName(),
                    UUID.fromString(empDocument.getUuid()), empDocument.getName()
                )
            )
            .flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
