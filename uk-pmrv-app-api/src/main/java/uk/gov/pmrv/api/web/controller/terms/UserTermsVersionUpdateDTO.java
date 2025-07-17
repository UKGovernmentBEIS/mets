package uk.gov.pmrv.api.web.controller.terms;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTermsVersionUpdateDTO {

    @NotNull(message = "{terms.version.notEmpty}")
    private Short version;

}