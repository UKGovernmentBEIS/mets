package uk.gov.pmrv.api.user.operator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PhoneNumberIntegrity;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PhoneNumberNotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class OperatorUserRegistrationDTO {

    @NotBlank(message = "{jwt.token.notEmpty}")
    private String emailToken;

    @NotBlank(message = "{userAccount.firstName.notEmpty}")
    @Size(max = 255, message = "{userAccount.firstName.typeMismatch}")
    private String firstName;

    @NotBlank(message = "{userAccount.lastName.notEmpty}")
    @Size(max = 255, message = "{userAccount.lastName.typeMismatch}")
    private String lastName;

    @PhoneNumberNotBlank(message = "{userAccount.phoneNumber.notEmpty}")
    @PhoneNumberIntegrity(message = "{userAccount.phoneNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO phoneNumber;

    @PhoneNumberIntegrity(message = "{userAccount.mobileNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO mobileNumber;

}
