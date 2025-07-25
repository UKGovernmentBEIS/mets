package uk.gov.pmrv.api.workflow.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentMethodRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public Set<PaymentMethodType> getPaymentMethodTypesByCa(CompetentAuthorityEnum competentAuthority) {
        return paymentMethodRepository.findByCompetentAuthority(competentAuthority).stream()
            .map(PaymentMethod::getType)
            .collect(Collectors.toSet());
    }
}
