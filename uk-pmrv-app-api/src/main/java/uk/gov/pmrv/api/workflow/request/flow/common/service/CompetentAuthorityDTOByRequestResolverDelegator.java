package uk.gov.pmrv.api.workflow.request.flow.common.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetentAuthorityDTOByRequestResolverDelegator {

    private final List<CompetentAuthorityDTOByRequestResolver> competentAuthorityDTOByRequestResolvers;

    public CompetentAuthorityDTO resolveCA(Request request, AccountType accountType) {
        return competentAuthorityDTOByRequestResolvers.stream()
				.filter(resolver -> accountType == resolver.getAccountType()).findFirst()
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, request, accountType)).resolveCA(request);
    }
}
