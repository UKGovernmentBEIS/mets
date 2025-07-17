package uk.gov.pmrv.api.notification.template.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.utils.MarkdownUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.EMAIL_TEMPLATE_NOT_FOUND;
import static uk.gov.netz.api.common.exception.ErrorCode.EMAIL_TEMPLATE_PROCESSING_FAILED;

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
     * @param templateName
     * @param competentAuthority the {@link CompetentAuthorityEnum}
     * @param accountType the {@link AccountType}
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     */
    @Transactional(readOnly = true)
    public NotificationContent processEmailNotificationTemplate(String templateName,
            CompetentAuthorityEnum competentAuthority, AccountType accountType, Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, competentAuthority, accountType, parameters, true);
    }

    /**
     * Process the provided template with the given parameters, using the FreeMarker Template Engine.
     * @param templateName
     * @param parameters {@link Map} that contains parameter names as keys and parameter objects as values
     * @return {@link NotificationContent} that encapsulates the processing result
     */
    @Transactional(readOnly = true)
    public NotificationContent processMessageNotificationTemplate(String templateName, 
            Map<String, Object> parameters) {
        return processNotificationTemplate(templateName, null, null, parameters, false);
    }

    private NotificationContent processNotificationTemplate(String templateName,
                                                            CompetentAuthorityEnum competentAuthority, AccountType accountType,
                                                            Map<String, Object> parameters, boolean parseToHtml) {

        NotificationTemplate notificationTemplate =  notificationTemplateRepository
            .findByNameAndCompetentAuthorityAndAccountType(PmrvNotificationTemplateName.getEnumValueFromName(templateName), competentAuthority, accountType)
            .orElseThrow(() -> new BusinessException(EMAIL_TEMPLATE_NOT_FOUND,
                String.format("Email Template %s Not Found for %s and %s account type",
                        templateName,
                        competentAuthority != null ? competentAuthority.name() : null,
                        accountType != null ? accountType.name() : null)));

        String processedSubject = processTemplateIntoString(templateName, notificationTemplate.getSubject(), parameters);
        String processedText = processTemplateIntoString(templateName, notificationTemplate.getText(), parameters);

        String finalText = parseToHtml ? MarkdownUtils.parseToHtml(processedText) : processedText;

        return NotificationContent.builder().subject(processedSubject).text(finalText).build();
    }

    private String processTemplateIntoString(String templateName, String text,
                                            Map<String, Object> model) {
        String result;
        try {

            Template template = new Template(templateName, new StringReader(text), freemarkerConfig);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Error during template processing, {}", e::getMessage);
            throw new BusinessException(EMAIL_TEMPLATE_PROCESSING_FAILED, templateName);
        }
        return StringEscapeUtils.escapeHtml4(result);
    }
}
