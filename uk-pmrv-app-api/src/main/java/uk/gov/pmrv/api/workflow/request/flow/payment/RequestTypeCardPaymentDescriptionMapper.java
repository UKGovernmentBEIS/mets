package uk.gov.pmrv.api.workflow.request.flow.payment;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public final class RequestTypeCardPaymentDescriptionMapper {

    static final Map<RequestType, String> cardPaymentDescriptions = new EnumMap<>(RequestType.class);

    static {
        cardPaymentDescriptions.put(RequestType.PERMIT_ISSUANCE, "Pay for a permit");
        cardPaymentDescriptions.put(RequestType.PERMIT_SURRENDER, "Pay for a permit surrender");
        cardPaymentDescriptions.put(RequestType.PERMIT_REVOCATION, "Pay for a permit revocation");
        cardPaymentDescriptions.put(RequestType.PERMIT_VARIATION, "Pay for a permit variation");
        cardPaymentDescriptions.put(RequestType.PERMIT_TRANSFER_A, "Pay for a permit transfer");
        cardPaymentDescriptions.put(RequestType.PERMIT_TRANSFER_B, "Pay for a permit transfer");
        cardPaymentDescriptions.put(RequestType.NER, "Pay new entrant reserve application fee");
        cardPaymentDescriptions.put(RequestType.DRE, "Pay reportable emissions fee");
        cardPaymentDescriptions.put(RequestType.EMP_ISSUANCE_UKETS, "Pay emissions monitoring plan application fee");
        cardPaymentDescriptions.put(RequestType.AVIATION_DRE_UKETS, "Pay emissions determination fee");
        cardPaymentDescriptions.put(RequestType.EMP_VARIATION_UKETS, "Pay emissions monitoring plan variation fee");
        cardPaymentDescriptions.put(RequestType.EMP_ISSUANCE_CORSIA, "Pay emissions monitoring plan application fee");
        cardPaymentDescriptions.put(RequestType.EMP_VARIATION_CORSIA, "Pay emissions monitoring plan variation fee");
    }

    public String getCardPaymentDescription(RequestType requestType) {
        return cardPaymentDescriptions.get(requestType);
    }
}
