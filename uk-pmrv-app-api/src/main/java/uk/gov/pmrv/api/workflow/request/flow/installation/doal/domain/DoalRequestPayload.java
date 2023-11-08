package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DoalRequestPayload extends RequestPayload {

    private Year reportingYear;

    private Doal doal;

    private DoalAuthority doalAuthority;

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;

    @Builder.Default
    private Map<UUID, String> doalAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> doalSectionsCompleted = new HashMap<>();
}
