import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { map, Observable, of } from 'rxjs';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationSubmittedRequestActionPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  CalculationNationalInventoryDataCalculationMethod,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
  EmissionSource,
  ReportingDataService,
  SourceStream,
} from 'pmrv-api';

@Component({
  selector: 'app-calculation-emissions-tier-summary',
  templateUrl: './calculation-emissions-tier-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationEmissionsTierSummaryComponent implements OnInit {
  @Input() isEditable = false;
  @Input() payload:
    | AerApplicationSubmitRequestTaskPayload
    | AerApplicationReviewRequestTaskPayload
    | AerApplicationVerificationSubmitRequestTaskPayload
    | AerApplicationSubmittedRequestActionPayload;
  @Input() index: number;
  @Input() areTiersExtraConditionsMet = false;

  sourceStreamEmission: CalculationSourceStreamEmission;
  sourceStream: SourceStream;
  emissionSources: EmissionSource[];
  chargingZone$: Observable<string>;
  sectorName$: Observable<string>;
  permitParamMonitoringTiers: any[];

  constructor(private readonly reportingDataService: ReportingDataService) {}

  ngOnInit(): void {
    this.sourceStreamEmission = (
      this.payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions
    )?.sourceStreamEmissions?.[this.index];

    this.sourceStream = this.payload.aer.sourceStreams.find(
      (sourceStream) => sourceStream.id === this.sourceStreamEmission?.sourceStream,
    );

    this.emissionSources = this.payload.aer.emissionSources.filter(
      (emissionSource) => this.sourceStreamEmission?.emissionSources?.includes(emissionSource.id) ?? false,
    );

    const postCode = (this.sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
      ?.postCode;

    const mainActivitySector = (
      this.sourceStreamEmission?.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod
    )?.mainActivitySector;

    this.chargingZone$ = postCode
      ? this.reportingDataService
          .getChargingZonesByPostCode(postCode)
          .pipe(
            map(
              (deliveryZones) =>
                deliveryZones.find(
                  (deliveryZone) =>
                    deliveryZone.code ===
                    (this.sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
                      ?.localZoneCode,
                )?.name ?? null,
            ),
          )
      : of(null);

    this.sectorName$ = mainActivitySector
      ? this.reportingDataService.getNationalInventoryData(this.payload?.reportingYear.toString()).pipe(
          map((nationalInventoryData) => {
            const sector = nationalInventoryData?.sectors?.find((sector) => {
              return sector.name === mainActivitySector;
            });

            return `${sector.name} ${sector.displayName}`;
          }),
        )
      : of(null);

    const sourceStreamEmissionId = this.sourceStreamEmission?.id;

    this.permitParamMonitoringTiers = sourceStreamEmissionId
      ? this.payload.permitOriginatedData?.permitMonitoringApproachMonitoringTiers
          ?.calculationSourceStreamParamMonitoringTiers?.[sourceStreamEmissionId]
      : [];
  }
}
