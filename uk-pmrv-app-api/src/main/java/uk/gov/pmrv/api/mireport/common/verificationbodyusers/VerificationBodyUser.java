package uk.gov.pmrv.api.mireport.common.verificationbodyusers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationBodyUser {

    @JsonProperty(value = "Verification body name")
    private String verificationBodyName;

    @JsonProperty(value = "Account status")
    private String accountStatus;

    @JsonProperty(value = "Accreditation reference number")
    private String accreditationReferenceNumber;

    @JsonProperty(value = "Is accredited for UK ETS Installations?")
    private Boolean isAccreditedForUKETSInstallations;

    @JsonProperty(value = "Is accredited for EU ETS Installations? ")
    private Boolean isAccreditedForEUETSInstallations;

    @JsonProperty(value = "Is accredited for UK ETS Aviations?")
    private Boolean isAccreditedForUKETSAviation;

    @JsonProperty(value = "Is accredited for CORSIA?")
    private Boolean isAccreditedForCorsia;

    @JsonProperty(value = "User role")
    private String role;

    @JsonProperty(value = "Name")
    private String verifierFullName;

    @JsonProperty(value = "Email")
    private String email;

    @JsonProperty(value = "Telephone")
    private String telephone;

    @JsonProperty(value = "User status")
    private String authorityStatus;

    @JsonProperty(value = "Last logon")
    private String lastLogon;

    private String userId;

    public static final List<String> getColumnNames() {
        return List.of("Verification body name", "Account status", "Accreditation reference number", "Is accredited for UK ETS Installations?",
                "Is accredited for EU ETS Installations? ", "Is accredited for UK ETS Aviations?", "Is accredited for CORSIA?",
                "User role", "Name", "Email", "Telephone", "User status", "Last logon");
    }
}
