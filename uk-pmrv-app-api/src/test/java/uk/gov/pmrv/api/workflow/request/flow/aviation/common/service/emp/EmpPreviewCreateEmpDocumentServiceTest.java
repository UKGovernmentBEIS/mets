package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia.EmpCorsiaPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsSourceData;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpPreviewCreateEmpDocumentServiceTest {


    @InjectMocks
    private EmpCorsiaPreviewCreateEmpDocumentService service;

    @Mock
    private DocumentTemplateEmpParamsProvider empParamsProvider;

    @Test
    void constructTemplateParams(){
        final Request request = Request.builder().build();
        final String signatory = "sig";
        final String empId = "empId";
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        final int consolidationNumber = 1;
        final Long accountId = 1L;
        EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia.builder().build();
        EmissionsMonitoringPlanCorsiaDTO empDTO =  EmissionsMonitoringPlanCorsiaDTO.builder()
                .id(empId)
                .empContainer(
                        EmissionsMonitoringPlanCorsiaContainer.builder()
                                .emissionsMonitoringPlan(emp)
                                .serviceContactDetails(serviceContactDetails)
                                .scheme(EmissionTradingScheme.CORSIA)
                                .build())
                .consolidationNumber(consolidationNumber)
                .accountId(accountId)
                .build();

        DocumentTemplateEmpParamsSourceData documentTemplateEmpParamsSourceData = DocumentTemplateEmpParamsSourceData.builder()
                .request(request)
                .signatory(signatory)
                .empContainer(empDTO.getEmpContainer())
                .consolidationNumber(consolidationNumber)
                .build();

        TemplateParams expectedTemplateParams = TemplateParams.builder()
                .permitId(empDTO.getId())
                .accountParams(AviationAccountTemplateParams.builder().name(null).location(null).build())
                .build();


        when(empParamsProvider.constructTemplateParams(documentTemplateEmpParamsSourceData)).thenReturn(TemplateParams.builder()
                .accountParams(AviationAccountTemplateParams.builder().build())
                .build());

        TemplateParams actualTemplateParams = service.constructTemplateParams(request,signatory,empDTO,consolidationNumber);

        assertEquals(expectedTemplateParams,actualTemplateParams);
        verify(empParamsProvider,times(1)).constructTemplateParams(documentTemplateEmpParamsSourceData);
    }
}
