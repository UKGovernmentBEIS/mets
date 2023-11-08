package uk.gov.pmrv.api.migration.aviationaccount.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AviationAccountHelper {

	public String constructQuery(String query, String ids) {
        StringBuilder queryBuilder = new StringBuilder(query);

        List<String> idList =
            !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" and r1.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }

	public static String constructErrorMessageWithCA(String accountId, String name, String displayId, String ca,
			String message, String data) {
		
		return "emitterId: " + accountId +
	            " | emitterDisplayId: " + displayId +
	            " | emitterName: " + name +
	            " | CA: " + ca +
	            " | Error: " + message +
	            " | data: " + data;
	}

	public static String constructSuccessMessage(String accountId, String name, String displayId, String ca) {
		return "emitterId: " + accountId +
	            " | emitterDisplayId: " + displayId +
	            " | emitterName: " + name +
	            " | CA: " + ca;
	    }
}
