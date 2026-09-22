package uk.gov.pmrv.api.settings.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.FeeHistory;
import uk.gov.pmrv.api.settings.domain.dto.FeeHistoryResponseDTO;
import uk.gov.pmrv.api.settings.domain.enumeration.FeeHistoryActionType;
import uk.gov.pmrv.api.settings.repository.FeeHistoryRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFee;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeHistoryServiceTest {

    @InjectMocks
    private FeeHistoryService service;

    @Mock
    private FeeHistoryRepository feeHistoryRepository;

    @Test
    void logFeeChange_withUser_savesHistoryWithUserDetails() {
        AppUser appUser = AppUser.builder().userId("user1").firstName("John").lastName("Doe").build();
        PaymentFeeMethod feeMethod = feeMethod();

        service.logFeeChange(feeMethod, FeeType.FIXED, FeeHistoryActionType.IMMEDIATE_UPDATE,
                new BigDecimal("1000"), new BigDecimal("1500"), null, appUser);

        ArgumentCaptor<FeeHistory> captor = ArgumentCaptor.forClass(FeeHistory.class);
        verify(feeHistoryRepository).save(captor.capture());
        FeeHistory saved = captor.getValue();
        assertThat(saved.getFeeMethod()).isEqualTo(feeMethod);
        assertThat(saved.getFeeType()).isEqualTo(FeeType.FIXED);
        assertThat(saved.getActionType()).isEqualTo(FeeHistoryActionType.IMMEDIATE_UPDATE);
        assertThat(saved.getUserId()).isEqualTo("user1");
        assertThat(saved.getChangedBy()).isEqualTo("John Doe");
        assertThat(saved.getOldAmount()).isEqualByComparingTo("1000");
        assertThat(saved.getNewAmount()).isEqualByComparingTo("1500");
        assertThat(saved.getEffectiveDate()).isNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void logFeeChange_withoutUser_savesHistoryWithSystemActor() {
        PaymentFeeMethod feeMethod = feeMethod();

        service.logFeeChange(feeMethod, FeeType.FIXED, FeeHistoryActionType.SYSTEM_APPLIED,
                new BigDecimal("1000"), new BigDecimal("1500"), null, null);

        ArgumentCaptor<FeeHistory> captor = ArgumentCaptor.forClass(FeeHistory.class);
        verify(feeHistoryRepository).save(captor.capture());
        FeeHistory saved = captor.getValue();
        assertThat(saved.getUserId()).isNull();
        assertThat(saved.getChangedBy()).isEqualTo("System");
    }

    @Test
    void logFeeChange_scheduledUpdate_savesEffectiveDate() {
        LocalDate effectiveDate = LocalDate.now().plusDays(30);
        AppUser appUser = AppUser.builder().userId("user1").firstName("Jane").lastName("Smith").build();
        PaymentFeeMethod feeMethod = feeMethod();

        service.logFeeChange(feeMethod, FeeType.HSE, FeeHistoryActionType.SCHEDULED_UPDATE,
                new BigDecimal("500"), new BigDecimal("600"), effectiveDate, appUser);

        ArgumentCaptor<FeeHistory> captor = ArgumentCaptor.forClass(FeeHistory.class);
        verify(feeHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getEffectiveDate()).isEqualTo(effectiveDate);
        assertThat(captor.getValue().getFeeType()).isEqualTo(FeeType.HSE);
    }

    @Test
    void getHistory_returnsPagedResults() {
        PaymentFeeMethod feeMethod = feeMethod();
        FeeHistory entry = FeeHistory.builder()
                .feeMethod(feeMethod)
                .feeType(FeeType.FIXED)
                .createdAt(LocalDateTime.now())
                .actionType(FeeHistoryActionType.IMMEDIATE_UPDATE)
                .changedBy("John Doe")
                .userId("user1")
                .oldAmount(new BigDecimal("1000"))
                .newAmount(new BigDecimal("1500"))
                .build();

        when(feeHistoryRepository.findByCompetentAuthorityAndRequestTypes(
                eq(CompetentAuthorityEnum.ENGLAND), any(), any()))
                .thenReturn(new PageImpl<>(List.of(entry), PageRequest.of(0, 30), 1));

        FeeHistoryResponseDTO result = service.getHistory(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 0, 30);

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getHistory()).hasSize(1);
        assertThat(result.getHistory().getFirst().getChangedBy()).isEqualTo("John Doe");
        assertThat(result.getHistory().getFirst().getRequestType()).isEqualTo(RequestType.PERMIT_ISSUANCE);
        assertThat(result.getHistory().getFirst().getActionType()).isEqualTo(FeeHistoryActionType.IMMEDIATE_UPDATE);
        assertThat(result.getHistory().getFirst().getOldAmount()).isEqualByComparingTo("1000");
        assertThat(result.getHistory().getFirst().getNewAmount()).isEqualByComparingTo("1500");
    }

    @Test
    void getHistory_filtersRequestTypesByAccountType() {
        when(feeHistoryRepository.findByCompetentAuthorityAndRequestTypes(
                eq(CompetentAuthorityEnum.ENGLAND), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 30), 0));

        service.getHistory(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 0, 30);

        ArgumentCaptor<List<RequestType>> captor = ArgumentCaptor.forClass(List.class);
        verify(feeHistoryRepository).findByCompetentAuthorityAndRequestTypes(
                eq(CompetentAuthorityEnum.ENGLAND), captor.capture(), any());
        assertThat(captor.getValue()).allMatch(rt -> rt.getAccountType() == AccountType.INSTALLATION);
    }

    private PaymentFeeMethod feeMethod() {
        EnumMap<FeeType, PaymentFee> fees = new EnumMap<>(FeeType.class);
        fees.put(FeeType.FIXED, PaymentFee.builder().amount(new BigDecimal("1000")).build());
        return PaymentFeeMethod.builder()
                .id(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .requestType(RequestType.PERMIT_ISSUANCE)
                .type(FeeMethodType.STANDARD)
                .fees(fees)
                .build();
    }
}
