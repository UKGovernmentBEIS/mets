import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, pluck } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { MeasurementOfCO2EmissionPointCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { areCategoryTierPrerequisitesMet } from '../../approaches/measurement/measurement-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-measurement',
  templateUrl: './measurement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  emissionPointCategoryAppliedTiers$ = this.store.findTask<MeasurementOfCO2EmissionPointCategoryAppliedTier[]>(
    'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
  );
  originalEmissionPointCategoryAppliedTiers$ =
    this.store.findOriginalTask<MeasurementOfCO2EmissionPointCategoryAppliedTier>(
      'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    );

  columns: GovukTableColumn[] = [
    { field: 'category', header: 'Emission point categories', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'measuredEmissions', header: 'Measured emissions', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'status', header: '', widthClass: 'app-column-width-15-per' },
  ];
  statusMap = statusMap;

  emissionPointCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
