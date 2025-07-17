import { AfterViewInit, ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { combineLatest, first, iif, map, Observable, of, shareReplay, switchMap } from 'rxjs';

import { AerUkEtsRequestActionPayload, requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { getApplicationSubmittedTasks, getRequestActionHeader } from '@aviation/request-action/util';
import { ComplianceMonitoringGroupComponent } from '@aviation/request-task/aer/ukets/aer-verify/tasks/compliance-monitoring/compliance-monitoring-group/compliance-monitoring-group.component';
import AerVerifierDetailsGroupFormComponent from '@aviation/request-task/aer/ukets/aer-verify/tasks/verifier-details/verifier-details-group-form/verifier-details-group-form.component';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template';
import { EmissionsReductionClaimSummaryTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-summary-template';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-summary-template';
import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { TotalEmissionsAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-aerodrome-pairs-table-template/total-emissions-aerodrome-pairs-table-template.component';
import { TotalEmissionsDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-domestic-flights-table-template/total-emissions-domestic-flights-table-template.component';
import { TotalEmissionsNonDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-non-domestic-flights-table-template/total-emissions-non-domestic-flights-table-template.component';
import { TotalEmissionsSchemeYearSummaryComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-scheme-year-summary';
import { TotalEmissionsStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-standard-fuels-table-template/total-emissions-standard-fuels-table-template.component';
import { TotalEmissionsSummaryTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-summary-template';
import { DataGapsMethodologiesGroupComponent } from '@aviation/shared/components/aer-verify/data-gaps-methodologies-group/data-gaps-methodologies-group.component';
import { AerVerifyMaterialityLevelGroupComponent } from '@aviation/shared/components/aer-verify/materiality-level-group/materiality-level-group.component';
import OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/changes-not-covered-in-emp/opinion-statement-changes-not-covered-in-emp-summary-template.component';
import OpinionStatementEmissionDetailsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/emission-details/opinion-statement-emission-details-summary-template.compmonent';
import OpinionStatementSiteVerificationSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/site-verification/opinion-statement-site-verification-summary-template.component';
import OpinionStatementTotalEmissionsSummaryTemplateComponent from '@aviation/shared/components/aer-verify/opinion-statement/total-emissions/opinion-statement-total-emissions-summary-template.component';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { VerifierCommentGroupComponent } from '@aviation/shared/components/aer-verify/verifier-comment-group/verifier-comment-group.component';
import { FUEL_TYPES } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { SharedModule } from '@shared/shared.module';
import { TaskSection } from '@shared/task-list/task-list.interface';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  AerodromePairsTotalEmissions,
  AviationAerDomesticFlightsEmissions,
  AviationAerNonDomesticFlightsEmissions,
  AviationAerOpinionStatement,
  AviationAerReportingObligationDetails,
  AviationAerSafPurchase,
  AviationAerTotalEmissions,
  AviationReportingService,
  LimitedCompanyOrganisation,
  RequestActionDTO,
  StandardFuelsTotalEmissions,
} from 'pmrv-api';

interface ViewModel {
  header: string;
  creationDate: string;
  sections: TaskSection<any>[];
  requestType: RequestActionDTO['requestType'];
  payload: AerUkEtsRequestActionPayload;
  certificationFiles?: AttachedFile[];
  evidenceFiles?: AttachedFile[];
  additionalDocumentsFiles?: AttachedFile[];
  purchases?: {
    purchase: AviationAerSafPurchase;
    files: AttachedFile[];
  }[];
  declarationFile?: AttachedFile;
  reportingData?: { reportingRequired: boolean; reportingObligationDetails?: AviationAerReportingObligationDetails };
  reportingFiles?: AttachedFile[];
  year?: number;
  totalEmissions$?: Observable<AviationAerTotalEmissions>;
  standardFuels$?: Observable<StandardFuelsTotalEmissions[]>;
  aerodromePairs$?: Observable<AerodromePairsTotalEmissions[]>;
  domesticFlights$?: Observable<AviationAerDomesticFlightsEmissions>;
  nonDomesticFlights$?: Observable<AviationAerNonDomesticFlightsEmissions>;
  fuelTypes?: { id: string; key: string; value: string }[];
}

@Component({
  selector: 'app-aer-ukets-request-action-report.component',
  templateUrl: './aer-ukets-request-action-report.component.html',
  standalone: true,
  imports: [
    SharedModule,
    AerVerifierDetailsGroupFormComponent,
    ComplianceMonitoringGroupComponent,
    OverallDecisionGroupComponent,
    AerMonitoringPlanVersionsComponent,
    OpinionStatementEmissionDetailsSummaryTemplateComponent,
    OpinionStatementTotalEmissionsSummaryTemplateComponent,
    OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent,
    OpinionStatementSiteVerificationSummaryTemplateComponent,
    UncorrectedItemGroupComponent,
    VerifierCommentGroupComponent,
    RecommendedImprovementsGroupComponent,
    DataGapsMethodologiesGroupComponent,
    AerVerifyMaterialityLevelGroupComponent,
    OperatorDetailsSummaryTemplateComponent,
    MonitoringApproachSummaryTemplateComponent,
    FlightDataTableComponent,
    AircraftTypesDataTableComponent,
    EmissionsReductionClaimSummaryTemplateComponent,
    DataGapsSummaryTemplateComponent,
    DataGapsListTemplateComponent,
    TotalEmissionsSummaryTemplateComponent,
    ReportingObligationSummaryTemplateComponent,
    TotalEmissionsStandardFuelsTableTemplateComponent,
    TotalEmissionsAerodromePairsTableTemplateComponent,
    TotalEmissionsDomesticFlightsTableTemplateComponent,
    TotalEmissionsNonDomesticFlightsTableTemplateComponent,
    TotalEmissionsSchemeYearSummaryComponent,
    AerMonitoringPlanChangesSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerUketsRequestActionReportComponent implements OnInit, AfterViewInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    switchMap(([requestAction, creationDate, regulatorViewer]) => {
      const aviationAerEmissionsCalculationDTO = {
        aggregatedEmissionsData: (requestAction.payload as AerUkEtsRequestActionPayload)?.aer?.aggregatedEmissionsData,
        saf: (requestAction.payload as AerUkEtsRequestActionPayload)?.aer?.saf,
      };

      return iif(
        () => (requestAction.payload as AerUkEtsRequestActionPayload).reportingRequired === true,
        combineLatest([
          this.aviationReportingService.getTotalEmissionsUkEts(aviationAerEmissionsCalculationDTO),
          this.aviationReportingService.getStandardFuelsEmissionsUkEts(aviationAerEmissionsCalculationDTO),
          this.aviationReportingService.getAerodromePairsEmissionsUkEts(aviationAerEmissionsCalculationDTO),
          this.aviationReportingService.getDomesticFlightsEmissionsUkEts(aviationAerEmissionsCalculationDTO),
          this.aviationReportingService.getNonDomesticFlightsEmissionsUkEts(aviationAerEmissionsCalculationDTO),
        ]),
        of(null),
      ).pipe(
        map((emissionsData) => {
          const [totalEmissions, standardFuels, aerodromePairs, domesticFlights, nonDomesticFlights] =
            emissionsData ?? [null, null, null, null, null];
          const aerUkEtsRequestActionPayload = requestAction.payload as AerUkEtsRequestActionPayload;
          return {
            header: getRequestActionHeader(requestAction.type, requestAction.payload),
            creationDate: creationDate,
            sections: getApplicationSubmittedTasks(requestAction.type, requestAction.payload, regulatorViewer, false),
            requestType: requestAction.requestType,
            payload: aerUkEtsRequestActionPayload,
            certificationFiles:
              aerUkEtsRequestActionPayload.aer?.operatorDetails?.airOperatingCertificate?.certificateFiles?.map(
                (uuid) => {
                  const file = aerUkEtsRequestActionPayload.aerAttachments[uuid];
                  return {
                    fileName: file,
                    downloadUrl: null,
                  };
                },
              ) ?? [],
            evidenceFiles:
              (
                aerUkEtsRequestActionPayload.aer?.operatorDetails.organisationStructure as LimitedCompanyOrganisation
              )?.evidenceFiles?.map((uuid) => {
                const file = aerUkEtsRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            additionalDocumentsFiles:
              aerUkEtsRequestActionPayload.aer?.additionalDocuments.documents?.map((uuid) => {
                const file = aerUkEtsRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            purchases: aerUkEtsRequestActionPayload.aer?.saf.exist
              ? aerUkEtsRequestActionPayload.aer?.saf.safDetails.purchases.map((item) => ({
                  purchase: item,
                  files:
                    item.evidenceFiles?.map((uuid) => {
                      const file = aerUkEtsRequestActionPayload.aerAttachments[uuid];
                      return {
                        fileName: file,
                        downloadUrl: null,
                      };
                    }) ?? [],
                }))
              : [],
            declarationFile: aerUkEtsRequestActionPayload.aer?.saf.exist
              ? {
                  fileName:
                    aerUkEtsRequestActionPayload.aerAttachments[
                      aerUkEtsRequestActionPayload.aer?.saf.safDetails.noDoubleCountingDeclarationFile
                    ],
                  downloadUrl: null,
                }
              : null,
            reportingData: {
              reportingRequired: aerUkEtsRequestActionPayload?.reportingRequired,
              reportingObligationDetails: aerUkEtsRequestActionPayload?.reportingObligationDetails,
            },
            reportingFiles:
              aerUkEtsRequestActionPayload?.reportingObligationDetails?.supportingDocuments?.map((uuid) => {
                const file = aerUkEtsRequestActionPayload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: null,
                };
              }) ?? [],
            year: aerUkEtsRequestActionPayload?.reportingYear,
            totalEmissions$: of(totalEmissions),
            standardFuels$: of(standardFuels),
            aerodromePairs$: of(aerodromePairs),
            domesticFlights$: of(domesticFlights),
            nonDomesticFlights$: of(nonDomesticFlights),
            fuelTypes: aerUkEtsRequestActionPayload?.verificationReport
              ? this.getFuelTypes(aerUkEtsRequestActionPayload?.verificationReport.opinionStatement.fuelTypes)
              : [],
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

  private getFuelTypes(
    fuelTypes: AviationAerOpinionStatement['fuelTypes'],
  ): { id: string; key: string; value: string }[] {
    return fuelTypes.map((ft) => {
      return {
        id: ft,
        key: FUEL_TYPES.find((f) => f.value === ft).summaryDescription,
        value: FUEL_TYPES.find((f) => f.value === ft).consumption,
      };
    });
  }
}
