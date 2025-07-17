import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, take } from 'rxjs';

import { permitTypeMapLowercase } from '@permit-application/shared/utils/permit';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { SectionsContainerAbstractComponent } from '../../permit-application/shared/sections/sections-container-abstract.component';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { BreadcrumbService } from '../../shared/breadcrumbs/breadcrumb.service';
import { PermitIssuanceStore } from '../store/permit-issuance.store';

@Component({
  selector: 'app-sections-container',
  templateUrl: './sections-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionsContainerComponent extends SectionsContainerAbstractComponent implements OnInit {
  isTaskTypeAmendsSubmit$ = this.store.pipe(
    map((state) => state.requestTaskType),
    map((requestTaskType) => requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'),
  );
  permitTypeMapLowercase = permitTypeMapLowercase;
  header$: Observable<string> = this.store.pipe(
    first(),
    map(
      (state) => `Apply for a ${state.permitType ? permitTypeMapLowercase?.[state.permitType] + ' permit' : 'permit'}`,
    ),
  );

  constructor(
    protected readonly store: PermitIssuanceStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly backLinkService: BackLinkService,
    protected readonly breadcrumbService: BreadcrumbService,
  ) {
    super(store, router, route, requestItemsService, requestActionsService);
  }

  ngOnInit(): void {
    this.init();
  }

  viewAmends(): void {
    this.requestActions$
      .pipe(
        map(
          (actions) => actions.filter((action) => action.type === 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS')[0],
        ),
        map((action) => action.id),
        take(1),
      )
      .subscribe((actionId) =>
        this.router.navigate(['/permit-issuance', 'action', actionId, 'review', 'return-for-amends'], {
          state: this.navigationState,
        }),
      );
  }
}
