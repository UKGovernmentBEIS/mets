package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpressionUtilsTest {
	
	@Mock
	private DelegateExecution execution;

	@Test
	void resolveMapFromJsonExpression() {
		String jsonExpr = "{\"reissueRequestSucceeded\": \"true\", \"accountId\": \"1\", \"reissueRequestId\": \"reissueRequest123\"}";
	
		Map<String, Object> result = ExpressionUtils.resolveMapFromJsonExpression(jsonExpr, execution);
		assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
				"reissueRequestSucceeded", "true",
				"accountId", "1",
				"reissueRequestId", "reissueRequest123"
				));
	}
}
