package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.mapper.PermitVariationAmendSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitVariationAmendSubmitMapper mapper =
        Mappers.getMapper(PermitVariationAmendSubmitMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        PermitVariationApplicationAmendsSubmitRequestTaskPayload permitVariationApplicationAmendsSubmitRequestTaskPayload =
            mapper.toPermitVariationAmendsSubmitRequestTaskPayload((PermitVariationRequestPayload) request.getPayload());
        permitVariationApplicationAmendsSubmitRequestTaskPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return permitVariationApplicationAmendsSubmitRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT);
    }
}