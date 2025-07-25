package uk.gov.pmrv.api.user.core.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds the user email.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDTO {

    @Email(message = "{userAccount.email.typeMismatch}")
    @Size(max = 255, message = "{userAccount.email.typeMismatch}")
    private String email;
}
