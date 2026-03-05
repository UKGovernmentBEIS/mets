import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { areCategoryTierPrerequisitesMet } from '../../approaches/calculation/calculation-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-calculation',
  templateUrl: './calculation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));

  sourceStreamCategoryAppliedTiers$ = this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
  );
  originalSourceStreamCategoryAppliedTiers$ = this.store.findOriginalTask<CalculationSourceStreamCategoryAppliedTier>(
    'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
  );

  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-one-third' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-third' },
    { field: 'activityData', header: 'Activity data', widthClass: 'govuk-!-width-one-third' },
    { field: 'netCalorificValue', header: 'Net calorific value', widthClass: 'govuk-!-width-one-third' },
    { field: 'emissionFactor', header: 'Emission factor', widthClass: 'govuk-!-width-one-third' },
    { field: 'oxidationFactor', header: 'Oxidation factor', widthClass: 'govuk-!-width-one-third' },
    { field: 'carbonContent', header: 'Carbon content', widthClass: 'govuk-!-width-one-third' },
    { field: 'conversionFactor', header: 'Conversion factor', widthClass: 'govuk-!-width-one-third' },
    { field: 'biomassFraction', header: 'Biomass fraction', widthClass: 'govuk-!-width-one-third' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-third' },
  ];
  statusMap = statusMap;

  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );

  notUsed = 'Not used';
  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
