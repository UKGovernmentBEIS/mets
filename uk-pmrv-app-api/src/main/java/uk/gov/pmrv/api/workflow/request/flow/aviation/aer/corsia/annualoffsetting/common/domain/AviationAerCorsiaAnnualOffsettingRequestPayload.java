package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain;


import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaAnnualOffsettingRequestPayload extends RequestPayload {

    private AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting;

    @Builder.Default
    private Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;
}
