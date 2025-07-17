package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"userId", "accountId", "accountType", "accountName", "accountStatus", "legalEntityName", "authorityStatus", "assignedRegulatorName", "crcoCode"})
public class AviationAccountAssignedRegulatorSiteContact extends AccountAssignedRegulatorSiteContact {
    @JsonProperty(value = "Account type")
    private String accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "CRCO code")
    private String crcoCode;

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status", "Legal Entity name", "User status", "Assigned regulator", "CRCO code");
    }
}
