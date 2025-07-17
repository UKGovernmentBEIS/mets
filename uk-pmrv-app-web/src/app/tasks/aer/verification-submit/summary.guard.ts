import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { getVerificationSectionStatus } from '@tasks/aer/core/aer-task-statuses';
import { complianceEtsWizard } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.wizard';
import { dataGapsWizardComplete } from '@tasks/aer/verification-submit/data-gaps/data-gaps.wizard';
import { materialityLevelWizard } from '@tasks/aer/verification-submit/materiality-level/materiality-level.wizard';
import { uncorrectedMisstatementsWizardComplete } from '@tasks/aer/verification-submit/misstatements/uncorrected-misstatements.wizard';
import { nonCompliancesWizardComplete } from '@tasks/aer/verification-submit/non-compliances/non-compliances.wizard';
import { nonConformitiesWizardComplete } from '@tasks/aer/verification-submit/non-conformities/non-conformities.wizard';
import { opinionStatementWizardComplete } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.wizard';
import { overallDecisionWizardComplete } from '@tasks/aer/verification-submit/overall-decision/overall-decision.wizard';
import { recommendedImprovementsWizardComplete } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements.wizard';
import { summaryOfConditionsWizardComplete } from '@tasks/aer/verification-submit/summary-of-conditions/summary-of-conditions.wizard';

@Injectable({ providedIn: 'root' })
export class SummaryGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return combineLatest([this.aerService.getPayload(), this.aerService.isEditable$]).pipe(
      first(),
      map(([payload, isEditable]) => {
        if (isEditable) {
          let isSummaryReady;
          switch (route.data.aerTask) {
            case 'opinionStatement':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                opinionStatementWizardComplete(payload.verificationReport?.opinionStatement);
              break;
            case 'etsComplianceRules':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                complianceEtsWizard(payload.verificationReport?.etsComplianceRules);
              break;
            case 'overallAssessment':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                overallDecisionWizardComplete(payload.verificationReport?.overallAssessment);
              break;
            case 'uncorrectedMisstatements':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                uncorrectedMisstatementsWizardComplete(payload.verificationReport?.uncorrectedMisstatements);
              break;
            case 'uncorrectedNonConformities':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                nonConformitiesWizardComplete(payload.verificationReport?.uncorrectedNonConformities);
              break;
            case 'methodologiesToCloseDataGaps':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                dataGapsWizardComplete(payload.verificationReport?.methodologiesToCloseDataGaps);
              break;
            case 'materialityLevel':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                materialityLevelWizard(payload.verificationReport?.materialityLevel);
              break;
            case 'summaryOfConditions':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                summaryOfConditionsWizardComplete(payload.verificationReport?.summaryOfConditions);
              break;
            case 'uncorrectedNonCompliances':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                nonCompliancesWizardComplete(payload.verificationReport?.uncorrectedNonCompliances);
              break;
            case 'recommendedImprovements':
              isSummaryReady =
                getVerificationSectionStatus(route.data.aerTask, payload) === 'complete' ||
                recommendedImprovementsWizardComplete(payload.verificationReport?.recommendedImprovements);
              break;
            default:
              isSummaryReady = getVerificationSectionStatus(route.data.aerTask, payload) !== 'not started';
          }

          return isSummaryReady || this.router.parseUrl(baseUrl);
        }
        return true;
      }),
    );
  }
}
