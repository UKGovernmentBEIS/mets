package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;

interface PaymentDetermineAmountService {

    BigDecimal determineAmount(Request request);

}
