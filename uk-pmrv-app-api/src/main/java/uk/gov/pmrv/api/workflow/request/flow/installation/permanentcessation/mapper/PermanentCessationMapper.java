package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermanentCessationMapper {

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    PermanentCessationApplicationSubmitRequestTaskPayload toPermanentCessationApplicationSubmitRequestTaskPayload(
            PermanentCessationRequestPayload requestPayload,
            RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "permanentCessation", source = "permanentCessation", qualifiedByName = "mapWithoutRegulatorComments")
    PermanentCessationApplicationSubmittedRequestActionPayload cloneWithoutRegulatorComments(
            PermanentCessationApplicationSubmittedRequestActionPayload requestActionPayload);

    @Named("mapWithoutRegulatorComments")
    default PermanentCessation mapWithoutRegulatorComments(PermanentCessation source) {
        if (source == null) return null;

        PermanentCessation target = new PermanentCessation();
        target.setFiles(source.getFiles());
        target.setCessationDate(source.getCessationDate());
        target.setCessationScope(source.getCessationScope());
        target.setDescription(source.getDescription());
        target.setAdditionalDetails(source.getAdditionalDetails());
        return target;
    }
}
