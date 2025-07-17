package uk.gov.pmrv.api.mireport.installation.accountsregulatorsitecontacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"userId", "accountId", "accountType", "accountName", "accountStatus", "legalEntityName", "authorityStatus", "assignedRegulatorName"})
public class InstallationAccountAssignedRegulatorSiteContact extends AccountAssignedRegulatorSiteContact {
    @JsonProperty(value = "Account type")
    private String accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Emitter type")
    private String emitterType;

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status", "Legal Entity name", "User status", "Assigned regulator", "Emitter type");
    }
}
