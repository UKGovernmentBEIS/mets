package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#subsidiaryCompanyExist) == (#subsidiaryCompanies?.size() gt 0)}", message = "emp.operatorDetails.subsidiaryCompanies")
public class EmpCorsiaOperatorDetails extends AviationCorsiaOperatorDetails {

    @NotNull
    @Valid
    private ActivitiesDescriptionCorsia activitiesDescription;

    @NotNull
    private Boolean subsidiaryCompanyExist;

    @Builder.Default
    @JsonDeserialize(as = ArrayList.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid SubsidiaryCompanyCorsia> subsidiaryCompanies = new ArrayList<>();

    @Override
    @JsonIgnore
    public Set<UUID> getAttachmentIds() {

        final Set<UUID> attachments = new HashSet<>();
        if (Boolean.TRUE.equals(subsidiaryCompanyExist) && subsidiaryCompanies != null && !subsidiaryCompanies.isEmpty()) {
        	subsidiaryCompanies.forEach(company -> {
        		if (company.getAirOperatingCertificate() != null && 
        				!ObjectUtils.isEmpty(company.getAirOperatingCertificate().getCertificateFiles())) {
        			attachments.addAll(company.getAirOperatingCertificate().getCertificateFiles());
        		}
        	});
        }
        attachments.addAll(super.getAttachmentIds());        
        return Collections.unmodifiableSet(attachments);
    }
}
