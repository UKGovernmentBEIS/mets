import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier } from 'pmrv-api';

import { areCategoryTierPrerequisitesMet } from '../../approaches/n2o/n2o-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-nitrous-oxide',
  templateUrl: './nitrous-oxide.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NitrousOxideComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));

  emissionPointCategoryAppliedTiers$ = this.store.findTask<MeasurementOfN2OEmissionPointCategoryAppliedTier[]>(
    'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
  );
  originalEmissionPointCategoryAppliedTiers$ = this.store.findOriginalTask<
    MeasurementOfN2OEmissionPointCategoryAppliedTier[]
  >('monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers');

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
