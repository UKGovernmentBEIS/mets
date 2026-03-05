import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { getSummaryDescription } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import {
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload,
  AviationAerCorsiaInternationalFlightsEmissions,
  AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO,
  AviationAerCorsiaInternationalFlightsEmissionsDetails,
  AviationReportingService,
} from 'pmrv-api';

import { createTablePage } from '../../../../../mi-reports/core/mi-report';

@Component({
  selector: 'app-total-emissions-corsia-state-pairs-table-template',
  templateUrl: './total-emissions-corsia-state-pairs-table-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsCorsiaStatePairsTableTemplateComponent {
  private aviationReportingService = inject(AviationReportingService);

  @Input() showAllData = false;
  @Input() internationalFlightsEmissions$: Observable<AviationAerCorsiaInternationalFlightsEmissions>;

  @Input() set corsiaRequestTaskPayload(
    requestTaskPayload:
      | AviationAerCorsiaApplicationSubmitRequestTaskPayload
      | AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload
      | AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload,
  ) {
    const aer = requestTaskPayload?.aer;
    const internationalFlightsEmissionsCalculationDTO: AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO = {
      year: requestTaskPayload?.reportingYear,
      aggregatedEmissionsData: aer?.aggregatedEmissionsData,
      emissionsReductionClaim: aer?.emissionsReductionClaim?.emissionsReductionClaimDetails?.totalEmissions,
    };
    const internationalFlightsEmissions$ =
      this.internationalFlightsEmissions$ ??
      this.aviationReportingService
        .getInternationalFlightsEmissionsCorsia(internationalFlightsEmissionsCalculationDTO)
        .pipe(shareReplay(1));
    this.pageItems$ = combineLatest([internationalFlightsEmissions$, this.currentPage$]).pipe(
      map(([items, currentPage]) =>
        this.showAllData
          ? items?.flightsEmissionsDetails
          : createTablePage(currentPage, this.pageSize, items?.flightsEmissionsDetails),
      ),
    );
    this.totalNumOfItems$ = internationalFlightsEmissions$.pipe(map((items) => items?.flightsEmissionsDetails?.length));
  }

  getSummaryDescription = getSummaryDescription;
  pageItems$: Observable<AviationAerCorsiaInternationalFlightsEmissionsDetails[]>;
  totalNumOfItems$: Observable<number>;
  currentPage$ = new BehaviorSubject<number>(1);
  pageSize = 30;
  columns: GovukTableColumn<AviationAerCorsiaInternationalFlightsEmissionsDetails>[] = [
    { header: 'Departure state', field: 'departureState' },
    { header: 'Arrival state', field: 'arrivalState' },
    { header: 'Number of flights', field: 'flightsNumber' },
    { header: 'Fuel used', field: 'fuelType' },
    { header: 'Offset', field: 'offset' },
    { header: 'Fuel consumption', field: 'fuelConsumption' },
    { header: 'Emissions', field: 'emissions' },
  ];
}
