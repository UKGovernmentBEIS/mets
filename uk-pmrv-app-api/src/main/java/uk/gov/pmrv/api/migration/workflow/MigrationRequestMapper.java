package uk.gov.pmrv.api.migration.workflow;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationRequestMapper {

    @Mapping(target = "id", source = "workflowEntityVO.workflowId")
    @Mapping(target = "status", source = "workflowEntityVO.status")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "verificationBodyId", ignore = true)
    Request toRequest(WorkflowEntityVO workflowEntityVO, Account account);

    default List<Request> toRequests(List<WorkflowEntityVO> etsWorkflows, Map<String, Account> accounts) {
        return etsWorkflows.stream()
                .filter(entityVO -> accounts.containsKey(entityVO.getEmitterId()))
                .map(entityVO -> toRequest(entityVO, accounts.get(entityVO.getEmitterId())))
                .collect(Collectors.toList());
    }

    @AfterMapping
    default void setMetadata(@MappingTarget Request request, WorkflowEntityVO workflowEntityVO, Account account) {
        switch (request.getType()) {
            case PERMIT_VARIATION -> {
                PermitVariationRequestMetadata metadata = PermitVariationRequestMetadata.builder().type(RequestMetadataType.PERMIT_VARIATION).build();
                if (request.getStatus() == RequestStatus.APPROVED) {
                    metadata.setPermitConsolidationNumber(workflowEntityVO.getPermitVersion());
                }
                request.setMetadata(metadata);
            }
            case PERMIT_ISSUANCE -> request.setMetadata(PermitIssuanceRequestMetadata.builder().type(RequestMetadataType.PERMIT_ISSUANCE).build());
            case PERMIT_SURRENDER -> request.setMetadata(PermitSurrenderRequestMetadata.builder().type(RequestMetadataType.PERMIT_SURRENDER).build());
            case PERMIT_NOTIFICATION -> request.setMetadata(PermitNotificationRequestMetadata.builder().type(RequestMetadataType.PERMIT_NOTIFICATION).build());
            case PERMIT_TRANSFER_B -> request.setMetadata(PermitTransferBRequestMetadata.builder().type(RequestMetadataType.PERMIT_TRANSFER_B).build());

            case EMP_VARIATION_UKETS, EMP_VARIATION_CORSIA -> {
                EmpVariationRequestMetadata metadata = EmpVariationRequestMetadata.builder().type(RequestMetadataType.EMP_VARIATION).build();
                if (request.getStatus() == RequestStatus.APPROVED) {
                    metadata.setEmpConsolidationNumber(workflowEntityVO.getPermitVersion());
                }
                request.setMetadata(metadata);
            }
            default -> {}
        }
    }
}
