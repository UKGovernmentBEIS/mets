package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload extends InstallationInspectionApplicationSubmitRequestTaskPayload {

}
