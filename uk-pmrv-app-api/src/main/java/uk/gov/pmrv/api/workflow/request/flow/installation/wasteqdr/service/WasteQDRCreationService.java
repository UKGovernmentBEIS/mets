package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRQuarter;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRCreationValidationService;


import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WasteQDRCreationService {

    private final WasteQDRCreationValidationService wasteQDRCreationValidationService;
    private final WasteQDRDueDateService wasteQDRDueDateService;
    private final DateService dateService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Request createWasteQDR(Long accountId) {

        Date expirationDate = wasteQDRDueDateService.generateDueDate();

        WasteQDRQuarter quarter = getRelevantQuarter();
        Year currentYear = dateService.getYear();
        Year wasteQDRyear = Objects.equals(WasteQDRQuarter.Q4, quarter) ? currentYear.minusYears(1) : currentYear;

        // Validate if Waste QDR is allowed
        RequestCreateValidationResult validationResult = wasteQDRCreationValidationService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED, validationResult);
        }

        return createWasteQDRForYearQuarter(accountId, wasteQDRyear, quarter, expirationDate);
    }


    private Request createWasteQDRForYearQuarter(Long accountId, Year wasteQDRYear, WasteQDRQuarter quarter, Date expirationDate) {
        // Validate if Waste QDR is allowed
        RequestCreateValidationResult validationYearQuarterResult = wasteQDRCreationValidationService.validateYearQuarter(accountId, wasteQDRYear, quarter);
        if(!validationYearQuarterResult.isValid()) {
            throw new BusinessException(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED, validationYearQuarterResult);
        }

        RequestCreateValidationResult emitterTypeValidationResult = wasteQDRCreationValidationService.validateAccountEmitterType(accountId);
        if(!emitterTypeValidationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED, emitterTypeValidationResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = Map.of(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE, expirationDate);
        return createWasteQDR(accountId, wasteQDRYear, quarter, processVars);
    }

    private Request createWasteQDR(Long accountId, Year wasteQDRYear, WasteQDRQuarter quarter, Map<String, Object> processVars) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.WASTE_QDR)
                .accountId(accountId)
                .requestPayload(WasteQDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.WASTE_QDR_REQUEST_PAYLOAD)
                        .qdr(WasteQDR.builder().build())
                        .build())
                .requestMetadata(WasteQDRRequestMetaData.builder()
                        .type(RequestMetadataType.WASTE_QDR)
                        .year(wasteQDRYear)
                        .quarter(quarter)
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(requestParams);
    }

    private WasteQDRQuarter getRelevantQuarter() {
        Month currentMonth = dateService.getLocalDate().getMonth();

        return switch (currentMonth) {
            case JANUARY, FEBRUARY, MARCH -> WasteQDRQuarter.Q4;
            case APRIL, MAY, JUNE -> WasteQDRQuarter.Q1;
            case JULY, AUGUST, SEPTEMBER -> WasteQDRQuarter.Q2;
            case OCTOBER, NOVEMBER, DECEMBER -> WasteQDRQuarter.Q3;
            default -> throw new IllegalStateException("Unexpected month: " + currentMonth);
        };
    }
}
