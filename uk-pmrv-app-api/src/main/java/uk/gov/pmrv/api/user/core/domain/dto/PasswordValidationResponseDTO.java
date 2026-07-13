package uk.gov.pmrv.api.user.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationResponseDTO {

    private boolean valid;

    private List<PasswordValidationErrorDTO> errors;
}
