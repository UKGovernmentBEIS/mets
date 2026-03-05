import { ChangeDetectionStrategy, Component } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { MeasurementOfCO2EmissionPointCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from './measurement-status';

@Component({
  selector: 'app-measurement',
  templateUrl: './measurement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementComponent {
  private readonly stateFeatures = toSignal(this.store.pipe(map((state) => state.features)));

  emissionPointCategoryAppliedTiers$ = this.store.findTask<MeasurementOfCO2EmissionPointCategoryAppliedTier[]>(
    'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
  );
  sumOfAnnualEmitted$ = this.emissionPointCategoryAppliedTiers$.pipe(
    map((appliedTiers) =>
      appliedTiers.reduce(
        (total, tier) => total + (Number(tier?.emissionPointCategory?.annualEmittedCO2Tonnes) || 0),
        0,
      ),
    ),
  );

  private readonly biomassFractionColumn = {
    field: 'biomassFraction',
    header: 'Biomass fraction',
    widthClass: 'govuk-!-width-one-quarter',
  };

  private readonly showBiomassColumn = this.stateFeatures()?.wastePermitEnabled;

  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Emission point categories', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'measuredEmissions', header: 'Measured emissions', widthClass: 'govuk-!-width-one-quarter' },
    ...(this.showBiomassColumn ? [this.biomassFractionColumn] : []),
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-quarter' },
  ];
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  statusMap = statusMap;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
