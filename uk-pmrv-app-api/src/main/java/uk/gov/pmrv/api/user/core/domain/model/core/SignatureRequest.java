package uk.gov.pmrv.api.user.core.domain.model.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureRequest {

    @NotEmpty
    private byte[] content;
    
    @NotBlank
    private String name;
    
    @NotNull
    @Positive
    private Long size;
    
    @NotBlank
    private String type;
}
