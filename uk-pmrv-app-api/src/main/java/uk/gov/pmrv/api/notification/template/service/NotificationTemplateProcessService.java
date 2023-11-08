package uk.gov.pmrv.api.notification.template.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.utils.MarkdownUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static uk.gov.pmrv.api.common.exception.ErrorCode.EMAIL_TEMPLATE_NOT_FOUND;
import static uk.gov.pmrv.api.common.exception.ErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILED;

/**
 * Service for processing notification templates using FreeMarker Template Engine.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class NotificationTemplateProcessService {

    private final Configuration freemarkerConfig;
    private final NotificationTemplateRepository notificationTemplateRepository;
    
    /**
     * Process the provided template with the given parameters, using the FreeMarker Template Engine.
     * @param templateName the {@link NotificationTemplateName}
     * @param competentAuthority the {@link CompetentAuthorityEnum}
     * @param accountType the {@link AccountType}
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     */
    @Transactional(readOnly = true)
    public NotificationContent processEmailNotificationTemplate(NotificationTemplateName templateName,
            CompetentAuthorityEnum competentAuthority, AccountType accountType, Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, competentAuthority, accountType, parameters, true);
    }

    /**
     * Process the provided template with the given parameters, using the FreeMarker Template Engine.
     * @param templateName the {@link NotificationTemplateName}
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     * @throws BusinessCheckedException the {@link BusinessCheckedException}
     */
    @Transactional(readOnly = true)
    public NotificationContent processMessageNotificationTemplate(NotificationTemplateName templateName, 
            Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, null, null, parameters, false);
    }

    private NotificationContent processNotificationTemplate(NotificationTemplateName templateName,
                                                            CompetentAuthorityEnum competentAuthority, AccountType accountType,
                                                            Map<String, Object> parameters, boolean parseToHtml) {

        NotificationTemplate notificationTemplate =  notificationTemplateRepository
            .findByNameAndCompetentAuthorityAndAccountType(templateName, competentAuthority, accountType)
            .orElseThrow(() -> new BusinessException(EMAIL_TEMPLATE_NOT_FOUND,
                String.format("Email Template %s Not Found for %s and %s account type",
                        templateName.getName(),
                        competentAuthority != null ? competentAuthority.name() : null,
                        accountType != null ? accountType.name() : null)));

        String processedSubject = processTemplateIntoString(templateName, notificationTemplate.getSubject(), parameters);
        String processedText = processTemplateIntoString(templateName,
            parseToHtml ? MarkdownUtils.parseToHtml(notificationTemplate.getText()) : notificationTemplate.getText(),
            parameters);

        return NotificationContent.builder().subject(processedSubject).text(processedText).build();
    }

    private String processTemplateIntoString(NotificationTemplateName templateName, String text,
                                            Map<String, Object> model) {
        String result;
        try {
            Template template = new Template(templateName.getName(), new StringReader(text), freemarkerConfig);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Error during template processing, {}", e::getMessage);
            throw new BusinessException(EMAIL_TEMPLATE_PROCESSING_FAILED, templateName.getName());
        }
        return result;
    }

}
