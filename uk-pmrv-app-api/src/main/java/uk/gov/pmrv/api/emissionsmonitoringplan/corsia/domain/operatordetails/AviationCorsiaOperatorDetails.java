package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AviationCorsiaOperatorDetails implements EmpCorsiaSection {

    @NotBlank
    @Size(max = 50)
    private String operatorName;

    @NotNull
    @Valid
    private FlightIdentification flightIdentification;

    @NotNull
    @Valid
    private AirOperatingCertificateCorsia airOperatingCertificate;

    @NotNull
    @Valid
    private OrganisationStructure organisationStructure;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {

        final Set<UUID> attachments = new HashSet<>();
        if (airOperatingCertificate != null && !ObjectUtils.isEmpty(airOperatingCertificate.getCertificateFiles())) {
            attachments.addAll(airOperatingCertificate.getCertificateFiles());
        }

        if (organisationStructure != null && !ObjectUtils.isEmpty(organisationStructure.getAttachmentIds())) {
            attachments.addAll(organisationStructure.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
