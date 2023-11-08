package uk.gov.pmrv.api.user.core.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.validation.Password;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
	
	@NotBlank(message = "{jwt.token.notEmpty}")
    String token;

    @NotBlank(message = "{otp.password.notEmpty}")
    @Pattern(regexp = "\\d{6}", message = "{otp.password.digits}")
    String otp;
	
	@NotBlank(message = "{userAccount.password.notEmpty}")
	@Password(message = "{userAccount.password.typeMismatch}")
    private String password;

}
