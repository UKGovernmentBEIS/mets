import { inject, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { PermitRoute } from '../permit-application/permit-route.interface';
import { DeterminationComponent } from '../permit-application/review/determination/determination.component';
import { DeterminationGuard } from '../permit-application/review/determination/determination.guard';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { PermitIssuanceActionGuard } from './permit-issuance-action.guard';
import { PermitIssuanceTaskGuard } from './permit-issuance-task.guard';
import { DecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { SummaryComponent } from './summary/summary.component';

const routes: PermitRoute[] = [
  {
    path: ':taskId',
    children: [
      {
        path: 'review',
        children: [
          {
            path: '',
            data: { breadcrumb: 'Permit determination' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Permit determination' },
                component: ReviewSectionsContainerComponent,
              },
              {
                path: 'determination',
                data: { pageTitle: 'Overall decision' },
                component: DeterminationComponent,
                canActivate: [DeterminationGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'change-assignee',
                loadChildren: () =>
                  import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
              },
            ],
          },
        ],
      },
      {
        path: '',
        data: { breadcrumb: ({ type }) => new TaskTypeToBreadcrumbPipe().transform(type) },
        resolve: { type: () => inject(PermitApplicationStore).getState().requestTaskType },
        children: [
          {
            path: '',
            component: SectionsContainerComponent,
          },
          {
            path: '',
            loadChildren: () =>
              import('../permit-application/permit-application.module').then((m) => m.PermitApplicationModule),
          },
          {
            path: 'summary',
            component: SummaryComponent,
          },
          {
            path: 'application-submitted',
            component: ApplicationSubmittedComponent,
          },
          {
            path: 'change-assignee',
            loadChildren: () =>
              import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
        ],
      },
    ],
    canActivate: [PermitIssuanceTaskGuard],
    canDeactivate: [PermitIssuanceTaskGuard],
  },

  {
    path: 'action/:actionId',
    data: { breadcrumb: { resolveText: ({ type }) => new ItemActionTypePipe().transform(type) } },
    resolve: { type: () => inject(PermitIssuanceStore).getState().requestActionType },
    children: [
      {
        path: '',
        component: SectionsContainerComponent,
      },
      {
        path: 'review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Permit determination' },
            component: ReviewSectionsContainerComponent,
          },
          {
            path: 'decision-summary',
            data: { pageTitle: 'Permit Decision Summary' },
            component: DecisionSummaryComponent,
          },
        ],
      },
      {
        path: '',
        loadChildren: () =>
          import('../permit-application/permit-application.module').then((m) => m.PermitApplicationModule),
      },
    ],
    canActivate: [PermitIssuanceActionGuard],
    canDeactivate: [PermitIssuanceActionGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitIssuanceRoutingModule {}
