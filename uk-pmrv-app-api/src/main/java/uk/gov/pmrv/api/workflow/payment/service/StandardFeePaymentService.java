package uk.gov.pmrv.api.workflow.payment.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.Objects;

@Service
public class StandardFeePaymentService extends PaymentService {

    private final InstallationAccountQueryService installationAccountQueryService;

    public StandardFeePaymentService(
        PaymentFeeMethodRepository paymentFeeMethodRepository,
        InstallationAccountQueryService installationAccountQueryService) {
        super(paymentFeeMethodRepository);
        this.installationAccountQueryService = installationAccountQueryService;
    }

    @Override
    public FeeMethodType getFeeMethodType() {
        return FeeMethodType.STANDARD;
    }

    @Override
    public FeeType resolveFeeType(Request request) {

        Long accountId = request.getAccountId();

        if (installationAccountQueryService.existsAccountById(accountId)){
            InstallationAccountDTO accountDTO = installationAccountQueryService
                    .getAccountDTOById(accountId);

            boolean isAccountWaste = Objects.nonNull(accountDTO.getEmitterType())  && accountDTO.getEmitterType().equals(EmitterType.WASTE);
            boolean isTransferWasteRequest = isTransferredRequestWaste(request);

            if (isAccountWaste || isTransferWasteRequest) {
                return FeeType.WASTE;
            }
        }

        return FeeType.FIXED;
    }

    private boolean isTransferredRequestWaste(Request request) {
        boolean isWaste = false;
        if(Objects.nonNull(request.getPayload())
                && request.getPayload().getPayloadType().equals(RequestPayloadType.PERMIT_TRANSFER_B_REQUEST_PAYLOAD)) {
            isWaste = ((PermitTransferBRequestPayload) request.getPayload()).getPermitType().equals(PermitType.WASTE);
        }
        return isWaste && Objects.nonNull(request.getType()) && request.getType().equals(RequestType.PERMIT_TRANSFER_B);
    }
}
