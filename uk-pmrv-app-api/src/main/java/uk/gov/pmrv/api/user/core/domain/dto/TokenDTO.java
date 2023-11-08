package uk.gov.pmrv.api.user.core.domain.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    @NotBlank(message = "{jwt.token.notEmpty}")
    private String token;
}
