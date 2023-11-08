package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = PermitIssuanceRequestMetadata.class, value = "PERMIT_ISSUANCE"),
                @DiscriminatorMapping(schema = PermitSurrenderRequestMetadata.class, value = "PERMIT_SURRENDER"),
                @DiscriminatorMapping(schema = PermitVariationRequestMetadata.class, value = "PERMIT_VARIATION"),
                @DiscriminatorMapping(schema = PermitNotificationRequestMetadata.class, value = "PERMIT_NOTIFICATION"),
                @DiscriminatorMapping(schema = PermitTransferBRequestMetadata.class, value = "PERMIT_TRANSFER_B"),
                @DiscriminatorMapping(schema = PermitBatchReissueRequestMetadata.class, value = "PERMIT_BATCH_REISSUE"),
                @DiscriminatorMapping(schema = ReissueRequestMetadata.class, value = "REISSUE"),
                @DiscriminatorMapping(schema = DoalRequestMetadata.class, value = "DOAL"),
                @DiscriminatorMapping(schema = AerRequestMetadata.class, value = "AER"),
                @DiscriminatorMapping(schema = VirRequestMetadata.class, value = "VIR"),
                @DiscriminatorMapping(schema = DreRequestMetadata.class, value = "DRE"),
                @DiscriminatorMapping(schema = AirRequestMetadata.class, value = "AIR"),

                // Aviation related request metadata
                @DiscriminatorMapping(schema = AviationAerRequestMetadata.class, value = "AVIATION_AER"),
                @DiscriminatorMapping(schema = AviationDreRequestMetadata.class, value = "AVIATION_DRE"),
                @DiscriminatorMapping(schema = AviationVirRequestMetadata.class, value = "AVIATION_VIR"),
                @DiscriminatorMapping(schema = EmpVariationRequestMetadata.class, value = "EMP_VARIATION"),
                @DiscriminatorMapping(schema = EmpBatchReissueRequestMetadata.class, value = "EMP_BATCH_REISSUE"),
                @DiscriminatorMapping(schema = AviationAerCorsiaRequestMetadata.class, value = "AVIATION_AER_CORSIA"),
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PermitIssuanceRequestMetadata.class, name = "PERMIT_ISSUANCE"),
        @JsonSubTypes.Type(value = PermitSurrenderRequestMetadata.class, name = "PERMIT_SURRENDER"),
        @JsonSubTypes.Type(value = PermitVariationRequestMetadata.class, name = "PERMIT_VARIATION"),
        @JsonSubTypes.Type(value = PermitNotificationRequestMetadata.class, name = "PERMIT_NOTIFICATION"),
        @JsonSubTypes.Type(value = PermitTransferBRequestMetadata.class, name = "PERMIT_TRANSFER_B"),
        @JsonSubTypes.Type(value = PermitBatchReissueRequestMetadata.class, name = "PERMIT_BATCH_REISSUE"),
        @JsonSubTypes.Type(value = ReissueRequestMetadata.class, name = "REISSUE"),
        @JsonSubTypes.Type(value = DoalRequestMetadata.class, name = "DOAL"),
        @JsonSubTypes.Type(value = AerRequestMetadata.class, name = "AER"),
        @JsonSubTypes.Type(value = VirRequestMetadata.class, name = "VIR"),
        @JsonSubTypes.Type(value = DreRequestMetadata.class, name = "DRE"),
        @JsonSubTypes.Type(value = AirRequestMetadata.class, name = "AIR"),

        // Aviation related request metadata
        @JsonSubTypes.Type(value = AviationAerRequestMetadata.class, name = "AVIATION_AER"),
        @JsonSubTypes.Type(value = AviationDreRequestMetadata.class, name = "AVIATION_DRE"),
        @JsonSubTypes.Type(value = AviationVirRequestMetadata.class, name = "AVIATION_VIR"),
        @JsonSubTypes.Type(value = EmpVariationRequestMetadata.class, name = "EMP_VARIATION"),
        @JsonSubTypes.Type(value = EmpBatchReissueRequestMetadata.class, name = "EMP_BATCH_REISSUE"),
        @JsonSubTypes.Type(value = AviationAerCorsiaRequestMetadata.class, name = "AVIATION_AER_CORSIA"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestMetadata {

    private RequestMetadataType type;
}
