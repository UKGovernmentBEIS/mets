package uk.gov.pmrv.api.notification.template.aviation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAccountTemplateParams extends AccountTemplateParams {

    private String crcoCode;
}
