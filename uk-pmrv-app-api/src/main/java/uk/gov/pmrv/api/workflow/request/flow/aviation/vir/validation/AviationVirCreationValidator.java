package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@Validated
@Service
@RequiredArgsConstructor
public class AviationVirCreationValidator {

    private final RequestQueryService requestQueryService;
    private final AviationVirRequestIdGenerator virRequestIdGenerator;

    @Transactional
    public RequestCreateValidationResult validate(@NotNull @Valid final VirVerificationData verificationData,
                                                  final Long accountId,
                                                  final Year year) {

        final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        // VIR exists
        final RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AviationVirRequestMetadata.builder().type(RequestMetadataType.AVIATION_VIR).year(year).build())
                .build();
        
        final String requestId = virRequestIdGenerator.generate(params);
        final boolean virExists = requestQueryService.existsRequestById(requestId);

        if (virExists) {
            validationResult.setValid(false);
            validationResult.getReportedRequestTypes().add(RequestType.AVIATION_VIR);
        }
        
        return validationResult;
    }
}
