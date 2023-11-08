package uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRequestIdGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Year;

@Validated
@Service
@RequiredArgsConstructor
public class VirCreationValidatorService {

    private final RequestQueryService requestQueryService;
    private final VirRequestIdGenerator virRequestIdGenerator;

    @Transactional
    public RequestCreateValidationResult validate(@NotNull @Valid VirVerificationData verificationData,
                                                  Long accountId, Year year,
                                                  PermitType permitType) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        // GHGE permit type for VIR creation
        if(!PermitType.GHGE.equals(permitType)) {
            validationResult.setValid(false);
            validationResult.getReportedRequestTypes().add(RequestType.AER);
        }

        // VIR exists
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(VirRequestMetadata.builder().type(RequestMetadataType.VIR).year(year).build())
                .build();
        String requestId = virRequestIdGenerator.generate(params);
        boolean virExists = requestQueryService.existsRequestById(requestId);

        if (virExists) {
            validationResult.setValid(false);
            validationResult.getReportedRequestTypes().add(RequestType.VIR);
        }

        return validationResult;
    }
}
