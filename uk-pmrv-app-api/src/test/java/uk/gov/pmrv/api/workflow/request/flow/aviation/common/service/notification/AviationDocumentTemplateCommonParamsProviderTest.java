package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.CompetentAuthorityProperties;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthorityDTOByRequestResolverDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDocumentTemplateCommonParamsProviderTest {

    @InjectMocks
    private AviationDocumentTemplateCommonParamsProvider aviationDocumentTemplateCommonParamsProvider;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private CompetentAuthorityProperties competentAuthorityProperties;

    @Mock
    private DateService dateService;

    @Mock
    private DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;

    @Mock
    private CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;

    @Test
    void constructCommonTemplateParams() throws IOException {
        LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 1, 1, 1);
        Long accountId = 1L;
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();

        EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder().build();
        Request request = Request.builder().id("1").accountId(accountId)
            .type(RequestType.EMP_ISSUANCE_UKETS)
            .submissionDate(submissionDate)
            .payload(requestPayload)
            .build();
        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder()
            .type(LocationType.ONSHORE_STATE)
            .line1("line1")
            .line2("line2")
            .city("city")
            .country("GR")
            .postcode("15125")
            .state("state")
            .build();
        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
            .name("accountname")
            .crcoCode("crco")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .accountType(AccountType.AVIATION)
            .location(location)
            .build();
        String signatory = "signatoryUserId";
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("email@email")
            .build();
        String empId = "empId";

        String caCentralInfo = "ca central info";

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId())).thenReturn(accountInfo);
        when(empQueryService.getEmpIdByAccountId(request.getAccountId())).thenReturn(Optional.of(empId));
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.of("primaryId"));
        when(userAuthService.getUserByUserId("primaryId")).thenReturn(accountPrimaryContact);
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.SERVICE))
            .thenReturn(Optional.of("serviceId"));
        when(userAuthService.getUserByUserId("serviceId"))
            .thenReturn(UserInfoDTO.builder()
                .firstName("service fn")
                .lastName("service ln")
                .userId("serviceContact")
                .email("serviceContact@email.com")
                .build());
        when(documentTemplateLocationInfoResolver.constructLocationInfo(accountInfo.getLocation())).thenReturn("location");

        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
            .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
            .signature(FileInfoDTO.builder().name("signature.pdf").uuid(UUID.randomUUID().toString()).build())
            .build();
        when(regulatorUserAuthService.getRegulatorUserById(signatory)).thenReturn(signatoryUser);
        when(competentAuthorityProperties.getCentralInfo()).thenReturn(caCentralInfo);

        FileDTO signatorySignature = FileDTO.builder()
            .fileContent("content".getBytes())
            .fileName("signature")
            .fileSize("content".length())
            .fileType("type")
            .build();
        when(userAuthService.getUserSignature(signatoryUser.getSignature().getUuid()))
            .thenReturn(Optional.of(signatorySignature));
        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2021, 1, 2, 1, 1));
        when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.AVIATION)).thenReturn(ca);


        //invoke
        TemplateParams result = aviationDocumentTemplateCommonParamsProvider.constructCommonTemplateParams(request, signatory);

        assertThat(result).isEqualTo(TemplateParams.builder()
            .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                .competentAuthority(ca)
                .logo(Files.readAllBytes(Paths.get("src", "main", "resources", "images", "ca", CompetentAuthorityEnum.ENGLAND.getLogoPath())))
                .build())
            .competentAuthorityCentralInfo(caCentralInfo)
            .signatoryParams(SignatoryTemplateParams.builder()
                .fullName(signatoryUser.getFullName())
                .jobTitle(signatoryUser.getJobTitle())
                .signature(signatorySignature.getFileContent())
                .build())
            .accountParams(AviationAccountTemplateParams.builder()
                .name(accountInfo.getName())
                .crcoCode(accountInfo.getCrcoCode())
                .accountType(accountInfo.getAccountType())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .primaryContact(accountPrimaryContact.getFullName())
                .primaryContactEmail(accountPrimaryContact.getEmail())
                .serviceContact("service fn service ln")
                .serviceContactEmail("serviceContact@email.com")
                .location("location")
                .build())
            .workflowParams(WorkflowTemplateParams.builder()
                .requestId("1")
                .requestType(RequestType.EMP_ISSUANCE_UKETS.name())
                .requestTypeInfo("your application for an Emissions Monitoring Plan")
                .requestSubmissionDate(Date.from(submissionDate.atZone(ZoneId.systemDefault()).toInstant()))
                .requestEndDate(LocalDateTime.of(2021, 1, 2, 1, 1))
                .build())
            .permitId(empId)
            .build());

        verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.AVIATION);
        verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(request.getAccountId());
        verify(empQueryService, times(1)).getEmpIdByAccountId(request.getAccountId());
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.SERVICE);
        verify(userAuthService, times(1)).getUserByUserId("primaryId");
        verify(userAuthService, times(1)).getUserByUserId("serviceId");
        verify(documentTemplateLocationInfoResolver, times(1)).constructLocationInfo(location);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(signatory);
        verify(userAuthService, times(1)).getUserSignature(signatoryUser.getSignature().getUuid());
        verify(competentAuthorityProperties, times(1)).getCentralInfo();
        verify(dateService, times(1)).getLocalDateTime();
    }
}