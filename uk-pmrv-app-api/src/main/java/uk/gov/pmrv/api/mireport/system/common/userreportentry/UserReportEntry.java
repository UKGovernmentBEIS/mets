package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"userAccountId", "fullName", "role", "userAccountStatus", "contactTypes", "email", "telephone", "mobile", "lastLogin"})
public class UserReportEntry {

    @JsonProperty("User Account ID")
    private String userAccountId;

    @JsonProperty("Name")
    private String fullName;

    @JsonProperty("User type")
    private String role;

    @JsonProperty("User Account status")
    private String userAccountStatus;

    @JsonProperty("Contact types")
    private List<String> contactTypes;

    @JsonProperty("Email")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonProperty("Telephone")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String telephone;

    @JsonProperty("Mobile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mobile;

    @JsonProperty("Last login")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastLogin;

    public static List<String> getColumnNames() {
        return List.of("User Account ID", "Name", "User type", "User Account status", "Contact types", "Email", "Telephone", "Mobile", "Last login");
    }
}
