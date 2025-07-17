package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload extends RequestActionPayload  {

    private AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting;

    @Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @NotNull
    private FileInfoDTO officialNotice;

}
