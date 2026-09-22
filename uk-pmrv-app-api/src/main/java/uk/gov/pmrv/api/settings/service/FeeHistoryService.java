package uk.gov.pmrv.api.settings.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.FeeHistory;
import uk.gov.pmrv.api.settings.domain.dto.FeeHistoryEntryDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeHistoryResponseDTO;
import uk.gov.pmrv.api.settings.domain.enumeration.FeeHistoryActionType;
import uk.gov.pmrv.api.settings.repository.FeeHistoryRepository;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Service
@RequiredArgsConstructor
public class FeeHistoryService {

    private static final String SYSTEM_ACTOR = "System";

    private final FeeHistoryRepository feeHistoryRepository;

    @Transactional
    public void logFeeChange(PaymentFeeMethod feeMethod, FeeType feeType, FeeHistoryActionType actionType,
                             BigDecimal oldAmount, BigDecimal newAmount, LocalDate effectiveDate, AppUser appUser) {
        feeHistoryRepository.save(FeeHistory.builder()
                .feeMethod(feeMethod)
                .feeType(feeType)
                .createdAt(LocalDateTime.now())
                .actionType(actionType)
                .userId(appUser != null ? appUser.getUserId() : null)
                .changedBy(appUser != null ? appUser.getFullName() : SYSTEM_ACTOR)
                .oldAmount(oldAmount)
                .newAmount(newAmount)
                .effectiveDate(effectiveDate)
                .build());
    }

    @Transactional(readOnly = true)
    public FeeHistoryResponseDTO getHistory(CompetentAuthorityEnum competentAuthority, AccountType accountType,
                                            Integer page, Integer pageSize) {
        List<RequestType> requestTypes = Arrays.stream(RequestType.values())
                .filter(rt -> rt.getAccountType() == accountType)
                .toList();

        Page<FeeHistory> historyPage = feeHistoryRepository.findByCompetentAuthorityAndRequestTypes(
                competentAuthority, requestTypes, PageRequest.of(page, pageSize));

        return FeeHistoryResponseDTO.builder()
                .history(historyPage.get().map(this::toDto).toList())
                .totalItems(historyPage.getTotalElements())
                .build();
    }

    private FeeHistoryEntryDTO toDto(FeeHistory fh) {
        return FeeHistoryEntryDTO.builder()
                .createdAt(fh.getCreatedAt())
                .changedBy(fh.getChangedBy())
                .actionType(fh.getActionType())
                .requestType(fh.getFeeMethod().getRequestType())
                .feeType(fh.getFeeType())
                .oldAmount(fh.getOldAmount())
                .newAmount(fh.getNewAmount())
                .effectiveDate(fh.getEffectiveDate())
                .build();
    }
}
