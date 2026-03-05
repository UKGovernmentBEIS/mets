import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from './n2o-status';

@Component({
  selector: 'app-n2o',
  templateUrl: './n2o.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class N2oComponent {
  emissionPointCategoryAppliedTiers$ = this.store.findTask<MeasurementOfN2OEmissionPointCategoryAppliedTier[]>(
    'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
  );
  sumOfAnnualEmitted$ = this.emissionPointCategoryAppliedTiers$.pipe(
    map((appliedTiers) =>
      appliedTiers.reduce(
        (total, tier) => total + (Number(tier?.emissionPointCategory?.annualEmittedCO2Tonnes) || 0),
        0,
      ),
    ),
  );
  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Emission point categories', widthClass: 'govuk-!-width-one-third' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-third' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-third' },
  ];
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  statusMap = statusMap;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
