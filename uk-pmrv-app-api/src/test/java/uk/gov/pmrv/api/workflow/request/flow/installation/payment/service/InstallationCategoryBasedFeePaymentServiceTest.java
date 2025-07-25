package uk.gov.pmrv.api.workflow.request.flow.installation.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType.CAT_A;
import static uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType.CAT_B;
import static uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType.HSE;

@ExtendWith(MockitoExtension.class)
class InstallationCategoryBasedFeePaymentServiceTest {

    @InjectMocks
    private InstallationCategoryBasedFeePaymentService installationCategoryBasedFeePaymentService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private PaymentFeeMethodRepository paymentFeeMethodRepository;

    @Test
    void getAmount() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        FeeMethodType feeMethodType = FeeMethodType.INSTALLATION_CATEGORY_BASED;
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.valueOf(30000)).build())
                .build())
            .build();
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .payload(requestPayload)
            .build();
        BigDecimal amount = BigDecimal.valueOf(2555.05);
        Map<FeeType, BigDecimal> fees = new EnumMap<>(FeeType.class);
        fees.put(CAT_A, amount);
        fees.put(CAT_B, BigDecimal.TEN);
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(feeMethodType)
            .fees(fees)
            .build();

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType))
            .thenReturn(Optional.of(paymentFeeMethod));

        assertEquals(amount, installationCategoryBasedFeePaymentService.getAmount(request));

        verify(paymentFeeMethodRepository, times(1))
            .findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType);
        verifyNoInteractions(installationAccountQueryService);
    }

    @Test
    void getAmount_not_found() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        FeeMethodType feeMethodType = FeeMethodType.INSTALLATION_CATEGORY_BASED;
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .build();

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType))
            .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
            () -> installationCategoryBasedFeePaymentService.getAmount(request));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());

        verify(paymentFeeMethodRepository, times(1))
            .findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType);
        verifyNoInteractions(installationAccountQueryService);
    }

    @Test
    void getFeeMethodType() {
        assertEquals(FeeMethodType.INSTALLATION_CATEGORY_BASED, installationCategoryBasedFeePaymentService.getFeeMethodType());
    }

    @Test
    void resolveFeeType_emitter_ghge() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .accountId(1L)
            .build();
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory accountInstallationCategory = InstallationCategory.A;
        InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder()
            .emitterType(emitterType)
            .installationCategory(accountInstallationCategory)
            .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId())).thenReturn(accountInfo);

        assertEquals(CAT_A, installationCategoryBasedFeePaymentService.resolveFeeType(request));

        verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(request.getAccountId());
    }

    @Test
    void resolveFeeType_emitter_hse() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .accountId(1L)
            .build();
        EmitterType emitterType = EmitterType.HSE;
        InstallationCategory accountInstallationCategory = InstallationCategory.N_A;
        InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder()
                .emitterType(emitterType)
                .installationCategory(accountInstallationCategory)
                .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId())).thenReturn(accountInfo);

        assertEquals(HSE, installationCategoryBasedFeePaymentService.resolveFeeType(request));

        verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(request.getAccountId());
    }

    @Test
    void resolveFeeType_when_request_type_permit_issuance() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.valueOf(60000)).build())
                .build())
            .build();
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .payload(requestPayload)
            .build();

        assertEquals(CAT_B, installationCategoryBasedFeePaymentService.resolveFeeType(request));

        verifyNoInteractions(installationAccountQueryService);
    }

    @Test
    void resolveFeeType_emitterWaste_returnWasteFeeType() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = RequestType.PERMIT_SURRENDER;
        Request request = Request.builder()
            .competentAuthority(competentAuthority)
            .type(requestType)
            .accountId(1L)
            .build();
        EmitterType emitterType = EmitterType.WASTE;
        InstallationCategory accountInstallationCategory = InstallationCategory.N_A;
        InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder()
                .emitterType(emitterType)
                .installationCategory(accountInstallationCategory)
                .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId())).thenReturn(accountInfo);

        assertEquals(FeeType.WASTE, installationCategoryBasedFeePaymentService.resolveFeeType(request));

        verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(request.getAccountId());
    }
}