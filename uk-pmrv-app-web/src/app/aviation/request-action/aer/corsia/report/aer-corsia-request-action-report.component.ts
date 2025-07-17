import { AfterViewInit, ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { combineLatest, first, iif, map, Observable, of, shareReplay, switchMap } from 'rxjs';

import { AerCorsiaRequestActionPayload, requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { getApplicationSubmittedTasks, getRequestActionHeader } from '@aviation/request-action/util';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { ConfidentialitySummaryTemplateComponent } from '@aviation/shared/components/aer/confidentiality-summary-template/confidentiality-summary-template.component';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { calculateAffectedFlightsDataGaps } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps-summary-template.component';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-corsia-summary-template';
import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';
import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-aerodrome-pairs-table-template/total-emissions-corsia-aerodrome-pairs-table-template.component';
import { TotalEmissionsCorsiaSchemeYearSummaryComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-scheme-year-summary';
import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-standard-fuels-table-template/total-emissions-corsia-standard-fuels-table-template.component';
import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from '@aviation/shared/components/aer-corsia/total-emissions-corsia-state-pairs-table-template/total-emissions-corsia-state-pairs-table-template.component';
import { EmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/emissions-reduction-claim-corsia-template/emissions-reduction-claim-corsia-template.component';
import { GeneralInformationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/general-information-corsia-template/general-information-corsia-template.component';
import { IndependentReviewCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/independent-review-corsia-template/independent-review-corsia-template.component';
import { MonitoringApproachVerifyCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/monitoring-approach-verify-corsia-template/monitoring-approach-verify-corsia-template.component';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group';
import { ProcessAnalysisCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/process-analysis-corsia-template/process-analysis-corsia-template.component';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { TimeAllocationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/time-allocation-corsia-template/time-allocation-corsia-template.component';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { VerifierDetailsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifier-details-corsia-template/verifier-details-corsia-template.component';
import { VerifiersConclusionsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifiers-conclusions-corsia-template/verifiers-conclusions-corsia-template.component';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { SharedModule } from '@shared/shared.module';
import { TaskSection } from '@shared/task-list/task-list.interface';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  AviationAerCorsiaAerodromePairsTotalEmissions,
  AviationAerCorsiaInternationalFlightsEmissions,
  AviationAerCorsiaStandardFuelsTotalEmissions,
  AviationAerCorsiaTotalEmissions,
  AviationAerReportingObligationDetails,
  AviationReportingService,
  LimitedCompanyOrganisation,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  header: string;
  creationDate: string;
  sections: TaskSection<any>[];
  requestType: RequestActionDTO['requestType'];
  payload: AerCorsiaRequestActionPayload;
  certificationFiles?: AttachedFile[];
  evidenceFiles?: AttachedFile[];
  cefFiles?: AttachedFile[];
  declarationFiles?: AttachedFile[];
  affectedFlightsDataGaps?: number;
  additionalDocumentsFiles?: AttachedFile[];
  totalEmissionsFiles?: AttachedFile[];
  aggregatedStatePairDataFiles?: AttachedFile[];
  totalEmissions?: AviationAerCorsiaTotalEmissions;
  reportingData?: { reportingRequired: boolean; reportingObligationDetails?: AviationAerReportingObligationDetails };
  reportingFiles?: AttachedFile[];
  year?: number;
  standardFuels$?: Observable<AviationAerCorsiaStandardFuelsTotalEmissions[]>;
  aerodromePairs$?: Observable<AviationAerCorsiaAerodromePairsTotalEmissions[]>;
  statePairs$?: Observable<AviationAerCorsiaInternationalFlightsEmissions>;
}

@Component({
  selector: 'app-aer-corsia-request-action-report.component',
  templateUrl: './aer-corsia-request-action-report.component.html',
  standalone: true,
  imports: [
    SharedModule,
    VerifierDetailsCorsiaTemplateComponent,
    TimeAllocationCorsiaTemplateComponent,
    GeneralInformationCorsiaTemplateComponent,
    ProcessAnalysisCorsiaTemplateComponent,
    MonitoringApproachVerifyCorsiaTemplateComponent,
    EmissionsReductionClaimCorsiaTemplateComponent,
    UncorrectedItemGroupComponent,
    RecommendedImprovementsGroupComponent,
    VerifiersConclusionsCorsiaTemplateComponent,
    OverallDecisionGroupComponent,
    IndependentReviewCorsiaTemplateComponent,
    ServiceContactDetailsSummaryTemplateComponent,
    OperatorDetailsSummaryTemplateComponent,
    AerMonitoringPlanVersionsComponent,
    AerMonitoringPlanChangesSummaryTemplateComponent,
    MonitoringApproachCorsiaSummaryTemplateComponent,
    FlightDataTableComponent,
    AircraftTypesDataTableComponent,
    AerEmissionsReductionClaimCorsiaTemplateComponent,
    DataGapsSummaryTemplateComponent,
    DataGapsListTemplateComponent,
    ConfidentialitySummaryTemplateComponent,
    TotalEmissionsCorsiaAerodromePairsTableTemplateComponent,
    TotalEmissionsCorsiaSchemeYearSummaryComponent,
    TotalEmissionsCorsiaStandardFuelsTableTemplateComponent,
    TotalEmissionsCorsiaStatePairsTableTemplateComponent,
    ReportingObligationSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerCorsiaRequestActionReportComponent implements OnInit, AfterViewInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    switchMap(([requestAction, creationDate, regulatorViewer]) => {
      const aviationAerCorsiaEmissionsCalculationDTO = {
        aggregatedEmissionsData: (requestAction.payload as AerCorsiaRequestActionPayload).aer?.aggregatedEmissionsData,
        emissionsReductionClaim: (requestAction.payload as AerCorsiaRequestActionPayload).aer?.emissionsReductionClaim
          ?.emissionsReductionClaimDetails?.totalEmissions,
      };

      const aviationAerCorsiaEmissionsCalculationDTOWithYear = {
        ...aviationAerCorsiaEmissionsCalculationDTO,
        year: (requestAction.payload as AerCorsiaRequestActionPayload).reportingYear,
      };

      return iif(
        () => (requestAction.payload as AerCorsiaRequestActionPayload).reportingRequired === true,
        combineLatest([
          this.aviationReportingService.getTotalEmissionsCorsia(aviationAerCorsiaEmissionsCalculationDTOWithYear),
          this.aviationReportingService.getStandardFuelsEmissionsCorsia(aviationAerCorsiaEmissionsCalculationDTO),
          this.aviationReportingService.getAerodromePairsEmissionsCorsia(
            aviationAerCorsiaEmissionsCalculationDTOWithYear,
          ),
          this.aviationReportingService.getInternationalFlightsEmissionsCorsia({
            ...aviationAerCorsiaEmissionsCalculationDTO,
            year: (requestAction.payload as AerCorsiaRequestActionPayload)?.reportingYear,
          }),
        ]),
        of(null),
      ).pipe(
        map((emissionsData) => {
          const [totalEmissions, standardFuels, aerodromePairs, statePairs] = emissionsData ?? [null, null, null, null];

          const aerCorsiaRequestActionPayload = requestAction.payload as AerCorsiaRequestActionPayload;
          return {
            header: getRequestActionHeader(requestAction.type, requestAction.payload),
            creationDate: creationDate,
            sections: getApplicationSubmittedTasks(requestAction.type, requestAction.payload, regulatorViewer, true),
            requestType: requestAction.requestType,
            payload: requestAction.payload as AerCorsiaRequestActionPayload,

            certificationFiles:
              aerCorsiaRequestActionPayload.aer?.operatorDetails?.airOperatingCertificate?.certificateFiles?.map(
                (uuid) => {
                  const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                  return {
                    fileName: file,
                    downloadUrl: null,
                  };
                },
              ) ?? [],
            evidenceFiles:
              (
                aerCorsiaRequestActionPayload.aer?.operatorDetails?.organisationStructure as LimitedCompanyOrganisation
              )?.evidenceFiles?.map((uuid) => {
                const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            cefFiles:
              aerCorsiaRequestActionPayload.aer?.emissionsReductionClaim?.emissionsReductionClaimDetails?.cefFiles?.map(
                (uuid) => {
                  const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                  return {
                    fileName: file,
                    downloadUrl: null,
                  };
                },
              ) ?? [],
            declarationFiles:
              aerCorsiaRequestActionPayload.aer?.emissionsReductionClaim?.emissionsReductionClaimDetails?.noDoubleCountingDeclarationFiles?.map(
                (uuid) => {
                  const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                  return {
                    fileName: file,
                    downloadUrl: null,
                  };
                },
              ) ?? [],
            affectedFlightsDataGaps: calculateAffectedFlightsDataGaps(
              aerCorsiaRequestActionPayload.aer?.dataGaps?.dataGapsDetails?.dataGaps ?? [],
            ),
            additionalDocumentsFiles:
              aerCorsiaRequestActionPayload.aer?.additionalDocuments.documents?.map((uuid) => {
                const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            totalEmissionsFiles:
              aerCorsiaRequestActionPayload.aer?.confidentiality.totalEmissionsDocuments?.map((uuid) => {
                const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            aggregatedStatePairDataFiles:
              aerCorsiaRequestActionPayload?.aer?.confidentiality.aggregatedStatePairDataDocuments?.map((uuid) => {
                const file = aerCorsiaRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            reportingData: {
              reportingRequired: aerCorsiaRequestActionPayload?.reportingRequired,
              reportingObligationDetails: aerCorsiaRequestActionPayload?.reportingObligationDetails,
            },
            reportingFiles:
              aerCorsiaRequestActionPayload?.reportingObligationDetails?.supportingDocuments?.map((uuid) => {
                const file = (requestAction.payload as any).aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            year: aerCorsiaRequestActionPayload?.reportingYear,
            totalEmissions,
            standardFuels$: of(standardFuels),
            aerodromePairs$: of(aerodromePairs),
            statePairs$: of(statePairs),
          };
        }),
      );
    }),
    first(),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly store: RequestActionStore,
    private titleService: Title,
    private aviationReportingService: AviationReportingService,
    private requestActionReportService: RequestActionReportService,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        requestActionQuery.selectRequestActionType,
        map((actionType) => {
          const actionTypePipe = new ItemActionTypePipe();
          return actionTypePipe.transform(actionType);
        }),
      )
      .subscribe((title) => this.titleService.setTitle(title));
  }

  ngAfterViewInit(): void {
    this.vm$.subscribe({
      complete: () => {
        //give some time to angular to render the data
        setTimeout(() => {
          this.requestActionReportService.print();
        }, 500);
      },
    });
  }
}
