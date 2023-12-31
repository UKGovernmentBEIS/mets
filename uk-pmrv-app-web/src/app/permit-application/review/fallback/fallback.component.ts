import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { FallbackSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { statusMap } from '../../../shared/task-list/task-item/status.map';
import { areCategoryTierPrerequisitesMet } from '../../approaches/fallback/fallback-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-fallback',
  templateUrl: './fallback.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((state) => state.groupKey));
  showDiff$ = this.store.showDiff$;

  sourceStreamCategoryAppliedTiers$ = this.store.findTask<FallbackSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.FALLBACK.sourceStreamCategoryAppliedTiers',
  );
  originalSourceStreamCategoryAppliedTiers$ = this.store.findOriginalTask<FallbackSourceStreamCategoryAppliedTier[]>(
    'monitoringApproaches.FALLBACK.sourceStreamCategoryAppliedTiers',
  );

  columns: GovukTableColumn<any>[] = [
    { field: 'category', header: 'Source stream categories', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'emissions', header: 'Emissions', widthClass: 'govuk-!-width-two-quarter' },
    { field: 'status', header: '', widthClass: 'app-column-width-15-per' },
  ];
  statusMap = statusMap;
  sourceStreamCategoriesStatus$ = this.store.pipe(
    map((state) => (!areCategoryTierPrerequisitesMet(state) ? 'cannot start yet' : 'not started')),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
