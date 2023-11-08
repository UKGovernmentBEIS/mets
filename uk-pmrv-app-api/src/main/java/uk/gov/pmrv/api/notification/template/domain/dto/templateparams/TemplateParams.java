package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class TemplateParams {
    
    @Valid
    private CompetentAuthorityTemplateParams competentAuthorityParams;
    
    private String competentAuthorityCentralInfo;
    
    @Valid
    private SignatoryTemplateParams signatoryParams;
    
    @Valid
    private AccountTemplateParams accountParams;

    private String permitId;

    @Valid
    private WorkflowTemplateParams workflowParams;

    @Builder.Default
    private Map<String, Object> params = new HashMap<>();
}
