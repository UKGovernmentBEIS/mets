package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AviationOperatorDetails {

    @NotBlank
    @Size(max = 255)
    private String operatorName;

    @NotBlank
    @Size(max = 255)
    private String crcoCode;

    @NotNull
    @Valid
    private FlightIdentification flightIdentification;

    @NotNull
    @Valid
    private AirOperatingCertificate airOperatingCertificate;

    @NotNull
    @Valid
    private OperatingLicense operatingLicense;

    @NotNull
    @Valid
    private OrganisationStructure organisationStructure;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        if (airOperatingCertificate != null && !ObjectUtils.isEmpty(airOperatingCertificate.getCertificateFiles())) {
            attachments.addAll(airOperatingCertificate.getCertificateFiles());
        }

        if (organisationStructure != null && !ObjectUtils.isEmpty(organisationStructure.getAttachmentIds())) {
            attachments.addAll(organisationStructure.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
