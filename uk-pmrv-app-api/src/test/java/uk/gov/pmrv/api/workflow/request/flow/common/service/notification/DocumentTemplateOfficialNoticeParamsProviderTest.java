package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationDocumentTemplateCommonParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSubmitDocumentTemplateWorkflowParamsProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateOfficialNoticeParamsProviderTest {

    private DocumentTemplateOfficialNoticeParamsProvider cut;

    @Mock
    private InstallationDocumentTemplateCommonParamsProvider installationDocumentTemplateCommonParamsProvider;
    
    @Mock
    private RfiSubmitDocumentTemplateWorkflowParamsProvider rfiSubmitDocumentTemplateWorkflowParamsProvider;

    @Spy
    private ArrayList<DocumentTemplateCommonParamsProvider> documentTemplateCommonParamsProviders;
    
    @Spy
    private ArrayList<DocumentTemplateWorkflowParamsProvider> workflowParamsProviders;
    
    @BeforeEach
    public void setUp() {
    	documentTemplateCommonParamsProviders.add(installationDocumentTemplateCommonParamsProvider);
        workflowParamsProviders.add(rfiSubmitDocumentTemplateWorkflowParamsProvider);
        cut = new DocumentTemplateOfficialNoticeParamsProvider(documentTemplateCommonParamsProviders, workflowParamsProviders);
    }

    @Test
    void constructTemplateParams() throws IOException {
        LocalDateTime submissionDate = LocalDateTime.now();
        String requestId = "1";
        Long accountId = 1L;
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .build();
        Request request = Request.builder().id(requestId).accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE).submissionDate(submissionDate)
                .payload(requestPayload)
                .build();
        InstallationAccountDTO account = InstallationAccountDTO.builder()
                .id(accountId)
                .name("accountname")
                .emitterType(EmitterType.GHGE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .siteName("accountsitename")
                .location(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("gridRef")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .line2("line2")
                                .city("city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .legalEntity(LegalEntityDTO.builder()
                        .name("lename")
                        .address(AddressDTO.builder()
                                .line1("le_line1")
                                .line2("le_line2")
                                .city("le_city")
                                .country("GR")
                                .postcode("15125")
                                .build())
                        .build())
                .build();
        String signatory = "signatoryUserId";
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        String permitId = "permitId";
        
        List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");
        
        DocumentTemplateParamsSourceData sourceParams = DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                .accountPrimaryContact(accountPrimaryContact)
                .request(request)
                .signatory(signatory)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails)
                .build();
        
        String caCentralInfo = "ca central info";

        RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
            .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
            .signature(FileInfoDTO.builder().name("signature.pdf").uuid(UUID.randomUUID().toString()).build())
            .build();

        FileDTO signatorySignature = FileDTO.builder()
            .fileContent("content".getBytes())
            .fileName("signature")
            .fileSize("content".length())
            .fileType("type")
            .build();

        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();
        TemplateParams commonTemplateParams = TemplateParams.builder()
            .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                .competentAuthority(ca)
                .logo(Files.readAllBytes(
                    Paths.get("src", "main", "resources", "images", "ca", CompetentAuthorityEnum.ENGLAND.getLogoPath())))
                .build())
            .competentAuthorityCentralInfo(caCentralInfo)
            .signatoryParams(SignatoryTemplateParams.builder()
                .fullName(signatoryUser.getFullName())
                .jobTitle(signatoryUser.getJobTitle())
                .signature(signatorySignature.getFileContent())
                .build())
            .accountParams(InstallationAccountTemplateParams.builder()
                .name(account.getName())
                .siteName(account.getSiteName())
                .emitterType(account.getEmitterType().name())
                .location("accountLocation")
                .legalEntityName(account.getLegalEntity().getName())
                .legalEntityLocation("leLocation")
                .primaryContact(accountPrimaryContact.getFullName())
                .serviceContact("service fn service ln")
                .serviceContactEmail("serviceContact@email.com")
                .build())
            .permitId(permitId)
            .build();

        when(installationDocumentTemplateCommonParamsProvider.getAccountType()).thenReturn(AccountType.INSTALLATION);
        when(installationDocumentTemplateCommonParamsProvider.constructCommonTemplateParams(request, signatory)).thenReturn(commonTemplateParams);
        when(rfiSubmitDocumentTemplateWorkflowParamsProvider.getContextActionType()).thenReturn(DocumentTemplateGenerationContextActionType.RFI_SUBMIT);
        when(rfiSubmitDocumentTemplateWorkflowParamsProvider.constructParams(requestPayload, requestId))
            .thenReturn(Map.of("questions", List.of("quest1", "quest2")));
        
        //invoke
        TemplateParams result = cut.constructTemplateParams(sourceParams);
        
        assertThat(result).isEqualTo(commonTemplateParams
            .withParams(Map.of(
                "toRecipient", accountPrimaryContact.getEmail(),
                "ccRecipients", ccRecipientsEmails,
                "questions", List.of("quest1", "quest2")
                )
            )
        );

        verify(installationDocumentTemplateCommonParamsProvider, times(1)).getAccountType();
        verify(installationDocumentTemplateCommonParamsProvider, times(1)).constructCommonTemplateParams(request, signatory);
        verify(rfiSubmitDocumentTemplateWorkflowParamsProvider, times(1)).getContextActionType();
        verify(rfiSubmitDocumentTemplateWorkflowParamsProvider, times(1)).constructParams(requestPayload, requestId);
    }
    
    @Test
    void constructTemplateParams_no_common_params_provider_found() throws IOException {
    	Request request = Request.builder().id("1").accountId(1L)
                .type(RequestType.PERMIT_ISSUANCE)
                .build();
    	
    	DocumentTemplateParamsSourceData sourceParams = DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                .request(request)
                .signatory("signatory")
                .build();
    	
    	when(installationDocumentTemplateCommonParamsProvider.getAccountType()).thenReturn(AccountType.AVIATION);
    	
    	BusinessException be = assertThrows(BusinessException.class, () -> cut.constructTemplateParams(sourceParams));
    	
    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_COMMON_PARAMS_PROVIDER_NOT_FOUND);
    	
    	verify(installationDocumentTemplateCommonParamsProvider, times(1)).getAccountType();
    }
}
