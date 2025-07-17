package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingRequestPayload extends RequestPayload {

    private AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting;

    @Builder.Default
    private Map<String, Boolean> aviationAerCorsia3YearPeriodOffsettingSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;
}
