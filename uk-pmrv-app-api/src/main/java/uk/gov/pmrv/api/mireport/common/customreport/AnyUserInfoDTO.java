package uk.gov.pmrv.api.mireport.common.customreport;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnyUserInfoDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberCode;
    private String lastLoginDate;
    private String email;
    private String jobTitle;

    public String getFullName() {
        return Stream.of(firstName, lastName).filter(value -> null != value).collect(Collectors.joining(" "));
    }

    public String getTelephone() {
        return (phoneNumberCode != null ? String.format("+%s", phoneNumberCode) : "") +
                (phoneNumber != null ? phoneNumber : "");
    }
}
