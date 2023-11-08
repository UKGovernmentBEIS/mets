package uk.gov.pmrv.api.workflow.request.flow.payment;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestTypeCardPaymentDescriptionMapperTest {

    @Test
    void getCardPaymentDescription() {
        assertEquals("Pay for a permit", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_ISSUANCE));
        assertEquals("Pay for a permit surrender", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_SURRENDER));
        assertEquals("Pay for a permit revocation", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_REVOCATION));
        assertEquals("Pay for a permit variation", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_VARIATION));
        assertEquals("Pay for a permit transfer", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_TRANSFER_A));
        assertEquals("Pay for a permit transfer", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.PERMIT_TRANSFER_B));
        assertEquals("Pay emissions monitoring plan application fee", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.EMP_ISSUANCE_UKETS));
        assertEquals("Pay emissions monitoring plan variation fee", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.EMP_VARIATION_UKETS));
        assertEquals("Pay emissions monitoring plan application fee", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.EMP_ISSUANCE_CORSIA));
        assertEquals("Pay emissions monitoring plan variation fee", RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.EMP_VARIATION_CORSIA));
        assertNull(RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(RequestType.INSTALLATION_ACCOUNT_OPENING));
    }
}