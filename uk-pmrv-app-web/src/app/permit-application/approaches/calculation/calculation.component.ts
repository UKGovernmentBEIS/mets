import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { CalculationOfCO2MonitoringApproach, CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from './calculation-status';

@Component({
  selector: 'app-calculation',
  templateUrl: './calculation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationComponent {
  sourceStreamCategoryAppliedTiers$ = this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
  );
  sumOfAnnualEmitted$ = this.sourceStreamCategoryAppliedTiers$.pipe(
    map((appliedTiers) =>
      appliedTiers.reduce(
        (total, tier) => total + (Number(tier?.sourceStreamCategory?.annualEmittedCO2Tonnes) || 0),
        0,
      ),
    ),
  );
  hasTransfer$ = this.store.pipe(
    map(
      (state) =>
        (state.permit?.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)?.hasTransfer,
    ),
  );
  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-one-third' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-third' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-third' },
  ];
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );
  statusMap = statusMap;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
