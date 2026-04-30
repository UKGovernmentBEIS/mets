package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.utils;

import org.flowable.engine.delegate.DelegateExecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Log4j2
public class ExpressionUtils {

    private final Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> resolveMapFromJsonExpression(String jsonExpr, DelegateExecution execution) {
        Matcher matcher = EL_PATTERN.matcher(jsonExpr);
        StringBuffer resolved = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = execution.getVariable(variableName);
            matcher.appendReplacement(resolved, value != null ? Matcher.quoteReplacement(value.toString()) : "");
        }

        matcher.appendTail(resolved);
        
	    Map<String, Object> map = null;
		try {
			map = mapper.readValue(resolved.toString(), Map.class);
		} catch (JsonProcessingException e) {
			log.error("Cannot parse to map the json expression: " + resolved.toString(), e);
		}
		
		return map;
    }
}
