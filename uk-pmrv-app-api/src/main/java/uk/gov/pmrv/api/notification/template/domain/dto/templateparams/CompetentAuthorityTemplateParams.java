package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetentAuthorityTemplateParams {

    @NotNull
    private CompetentAuthorityDTO competentAuthority;
    
    @NotEmpty
    private byte[] logo;
    
    public String getName() {
        return competentAuthority.getName();
    }
    
    public String getEmail() {
        return competentAuthority.getEmail();
    }
}
