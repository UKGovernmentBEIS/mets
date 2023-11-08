package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "scheme", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmissionsMonitoringPlanUkEtsContainer.class, name = "UK_ETS_AVIATION"),
    @JsonSubTypes.Type(value = EmissionsMonitoringPlanCorsiaContainer.class, name = "CORSIA")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class EmissionsMonitoringPlanContainer {

    private EmissionTradingScheme scheme;

    @Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();
}
