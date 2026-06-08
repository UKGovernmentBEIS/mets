package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserReportInfoDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberCode;
    private String lastLoginDate;
    private String email;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getTelephone() {
        return Optional.ofNullable(phoneNumber)
                .map(number ->
                        phoneNumberCode != null ? String.format("+%s%s", phoneNumberCode, number) : String.format("+%s", number))
                .orElse(null);
    }
}
