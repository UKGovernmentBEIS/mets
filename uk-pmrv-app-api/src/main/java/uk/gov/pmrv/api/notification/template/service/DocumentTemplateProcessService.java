package uk.gov.pmrv.api.notification.template.service;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.TraversalUtil.Callback;
import org.docx4j.XmlUtils;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.Numbering;
import org.docx4j.wml.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j2
@Validated
@Service
@RequiredArgsConstructor
public class DocumentTemplateProcessService {

    private final DocumentGeneratorClientService documentGeneratorClientService;
    private final FreemarkerTemplateEngine freemarkerTemplateEngine;

    @Timed(value = "document.generation")
    public byte[] generateFileDocumentFromTemplate(@Valid FileDTO fileDocumentTemplate, @Valid TemplateParams templateParams, String fileNameToGenerate)
          throws DocumentTemplateProcessException {
		try {
			byte[] processedDocument = processDocumentTemplate(fileDocumentTemplate, templateParams);
			byte[] postProcessedDocument = postProcessDocument(processedDocument);
			return documentGeneratorClientService.generateDocument(postProcessedDocument, fileNameToGenerate);
		} catch (Exception e) {
			log.error("Error when generation file from template", e);
			throw new DocumentTemplateProcessException(e.getMessage());
		}
    }
    
    private byte[] processDocumentTemplate(@Valid FileDTO fileDocumentTemplate, @Valid TemplateParams templateParams) throws Exception {
    	try (final InputStream inputStream = new ByteArrayInputStream(fileDocumentTemplate.getFileContent());
                final BufferedInputStream inputBufferedStream = new BufferedInputStream(inputStream);
                final ByteArrayOutputStream processedOutputStream = new ByteArrayOutputStream();
                final BufferedOutputStream processedBufferedOutputStream = new BufferedOutputStream(processedOutputStream)
          ) {
              IXDocReport report = XDocReportRegistry.getRegistry().loadReport(inputBufferedStream, freemarkerTemplateEngine);
              IContext context = report.createContext();
              FieldsMetadata metadata = report.createFieldsMetadata();

              metadata.addFieldAsImage("competentAuthorityLogo");
              context.put("competentAuthorityLogo",
                    new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getCompetentAuthorityParams().getLogo()), false));
              metadata.addFieldAsImage("competentAuthorityLogo2");//TODO: find a way to reference the image inside the template mutliple times
              context.put("competentAuthorityLogo2",
                    new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getCompetentAuthorityParams().getLogo()), false));

              //add signatory signature
              metadata.addFieldAsImage("signature");
              context.put("signature", new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getSignatoryParams().getSignature()), true));
              metadata.addFieldAsImage("signature2");//TODO:
              context.put("signature2", new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getSignatoryParams().getSignature()), true));
              context.put("signature3", new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getSignatoryParams().getSignature()), true));
              metadata.addFieldAsImage("signature3");

              // add params
              context.put("competentAuthority", templateParams.getCompetentAuthorityParams());
              context.put("competentAuthorityCentralInfo", templateParams.getCompetentAuthorityCentralInfo());
              context.put("signatory", templateParams.getSignatoryParams());
              context.put("account", templateParams.getAccountParams());
              context.put("permitId", templateParams.getPermitId());
              context.put("currentDate", new Date());
              context.put("workflow", templateParams.getWorkflowParams());
              context.put("params", templateParams.getParams());
              
              report.process(context, processedBufferedOutputStream);
              
              return processedOutputStream.toByteArray();
          } 
    }
    
    private byte[] postProcessDocument(byte[] processedDocument) throws Exception {
        try (final BufferedInputStream processedBufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(processedDocument));
        	  final ByteArrayOutputStream postProcessedBufferedOutputStream = new ByteArrayOutputStream()
        ) {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(processedBufferedInputStream);
            Mapper fontMapper = new IdentityPlusMapper();
            clearEmptyCommandLines(wordMLPackage);
            replaceNumberingsWithSymbolFontIfAny(wordMLPackage);
            wordMLPackage.setFontMapper(fontMapper);

            Docx4J.save(wordMLPackage, postProcessedBufferedOutputStream);
            
			return postProcessedBufferedOutputStream.toByteArray();
        }
    }

    private void clearEmptyCommandLines(WordprocessingMLPackage wordMLPackage) {
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart.getJaxbElement();
        Body body = wmlDocumentEl.getBody();

        new TraversalUtil(body,
                new Callback() {

                    @Override
                    public List<Object> apply(Object o) {
                        return null;
                    }

                    @Override
                    public boolean shouldTraverse(Object o) {
                        return true;
                    }

                    @Override
                    public void walkJAXBElements(Object parent) {

                        List children = getChildren(parent);
                        if (children != null) {
                            List<P> paragraphsToRemove = new ArrayList<>();
                            for (Object o : children) {
                                o = XmlUtils.unwrap(o);
                                if (o instanceof org.docx4j.wml.P) {
                                    P p = (org.docx4j.wml.P) o;
                                    if (p.getPPr() != null && p.getPPr().getPStyle() != null) {
                                        if ("TemplateCommand".equals(p.getPPr().getPStyle().getVal())) {
                                            paragraphsToRemove.add((P)o);
                                            continue;
                                        }
                                    }
                                }

                                if (this.shouldTraverse(o)) {
                                    walkJAXBElements(o);
                                }
                            }
                            
                            if (paragraphsToRemove.size() > 0) {
                                List<Object> parentAsList = null;
                                if (parent instanceof List) {
                                    parentAsList = (List)parent;
                                } else if (parent instanceof ContentAccessor) {
                                    parentAsList = ((ContentAccessor)parent).getContent();
                                }
                                if (parentAsList != null)
                                    for (P p: paragraphsToRemove)
                                        parentAsList.remove(p);
                            }
                        }
                    }

                    @Override
                    public List<Object> getChildren(Object o) {
                        return TraversalUtil.getChildrenImpl(o);
                    }
                }
        );
    }

    private void replaceNumberingsWithSymbolFontIfAny(WordprocessingMLPackage wordMLPackage) {
        NumberingDefinitionsPart numberingDefinitionsPart = wordMLPackage.getMainDocumentPart().getNumberingDefinitionsPart();

        if (Objects.nonNull(numberingDefinitionsPart)) {
            Numbering numbering = numberingDefinitionsPart.getJaxbElement();

            for (Numbering.AbstractNum abstractNumNode : numbering.getAbstractNum()) {
                for (Lvl lvl : abstractNumNode.getLvl()) {
                    if (lvl.getRPr() != null && lvl.getRPr().getRFonts() != null && lvl.getNumFmt().getVal() == NumberFormat.BULLET) {
                    	String SYMBOL_FONT = "Symbol";
                        lvl.getRPr().getRFonts().setAscii(SYMBOL_FONT);
                        lvl.getRPr().getRFonts().setHAnsi(SYMBOL_FONT);
                    }
                }
            }
        }
    }
}
