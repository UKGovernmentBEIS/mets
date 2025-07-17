import { inject, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { TaskGuard } from '@tasks/task.guard';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { PermitRoute } from '../permit-application/permit-route.interface';
import { DeterminationComponent } from '../permit-application/review/determination/determination.component';
import { DeterminationGuard } from '../permit-application/review/determination/determination.guard';
import { AboutVariationComponent } from './about-variation/about-variation.component';
import { AboutVariationGuard } from './about-variation/about-variation.guard';
import { AnswersComponent as AboutVariationAnswersComponent } from './about-variation/answers/answers.component';
import { AnswersGuard as PermitVariationDetailsAnswersGuard } from './about-variation/answers/answers.guard';
import { ChangesComponent } from './about-variation/changes/changes.component';
import { ChangesGuard } from './about-variation/changes/changes.guard';
import { SummaryComponent as AboutVariationSummaryComponent } from './about-variation/summary/summary.component';
import { SummaryGuard as PermitVariationDetailsSummaryGuard } from './about-variation/summary/summary.guard';
import { PermitVariationActionGuard } from './permit-variation-action.guard';
import { PermitVariationTaskGuard } from './permit-variation-task.guard';
import { AboutVariationComponent as ReviewAboutVariation } from './review/about-variation/about-variation.component';
import { DecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { LogChangesComponent } from './review/determination/log-changes/log-changes.component';
import { LogChangesGuard } from './review/determination/log-changes/log-changes.guard';
import { ReasonTemplateComponent } from './review/determination/reason-template/reason-template.component';
import { ReasonTemplateGuard } from './review/determination/reason-template/reason-template.guard';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { PermitVariationStore } from './store/permit-variation.store';
import { SummaryComponent } from './summary/summary.component';

const routes: PermitRoute[] = [
  {
    path: ':taskId',
    children: [
      {
        path: 'review',
        data: { breadcrumb: 'Variation determination' },
        children: [
          {
            path: '',
            component: ReviewSectionsContainerComponent,
          },
          {
            path: 'about',
            data: { pageTitle: 'About the variation', groupKey: 'ABOUT_VARIATION' },
            component: ReviewAboutVariation,
          },
          {
            path: 'determination',
            data: { statusKey: 'determination' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Overall decision' },
                component: DeterminationComponent,
                canActivate: [DeterminationGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'log-changes',
                data: { pageTitle: 'Overall decision variation log changes' },
                component: LogChangesComponent,
                canActivate: [LogChangesGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'reason-template',
                data: { pageTitle: 'Overall decision variation reason template' },
                component: ReasonTemplateComponent,
                canActivate: [ReasonTemplateGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'change-assignee',
            canActivate: [TaskGuard],
            loadChildren: () =>
              import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
          {
            path: 'cancel',
            loadChildren: () => import('../cancel-task/cancel-task.module').then((m) => m.CancelTaskModule),
          },
        ],
      },
      {
        path: '',
        data: {
          breadcrumb: {
            resolveText: ({ type }) => new TaskTypeToBreadcrumbPipe().transform(type),
          },
        },
        resolve: { type: () => inject(PermitVariationStore).getState().requestTaskType },
        children: [
          {
            path: '',
            component: SectionsContainerComponent,
          },
          {
            path: 'about',
            children: [
              {
                path: '',
                data: { pageTitle: 'About variation - Describe the details' },
                component: AboutVariationComponent,
                canActivate: [AboutVariationGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'changes',
                data: { pageTitle: 'About variation - What type of changes are you making?', backlink: '../' },
                component: ChangesComponent,
                canActivate: [ChangesGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'answers',
                data: { pageTitle: 'About variation - Answers' },
                component: AboutVariationAnswersComponent,
                canActivate: [PermitVariationDetailsAnswersGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'summary',
                data: { pageTitle: 'About variation - Summary', breadcrumb: 'About the variation' },
                canActivate: [PermitVariationDetailsSummaryGuard],
                component: AboutVariationSummaryComponent,
              },
            ],
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
            path: 'cancel',
            loadChildren: () => import('../cancel-task/cancel-task.module').then((m) => m.CancelTaskModule),
          },
          {
            path: 'change-assignee',
            loadChildren: () =>
              import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
        ],
      },
    ],
    canActivate: [PermitVariationTaskGuard],
    canDeactivate: [PermitVariationTaskGuard],
  },

  {
    path: 'action/:actionId',
    data: { breadcrumb: { resolveText: ({ type }) => new ItemActionTypePipe().transform(type) } },
    resolve: { type: () => inject(PermitVariationStore).getState().requestActionType },
    children: [
      {
        path: '',
        component: SectionsContainerComponent,
      },
      {
        path: 'about',
        children: [
          {
            path: 'summary',
            data: { pageTitle: 'About variation - Summary' },
            canActivate: [PermitVariationDetailsSummaryGuard],
            component: AboutVariationSummaryComponent,
          },
        ],
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
            path: 'about',
            data: { pageTitle: 'About the variation', groupKey: 'ABOUT_VARIATION' },
            component: ReviewAboutVariation,
          },
          {
            path: 'decision-summary',
            data: { pageTitle: 'Permit Variation Decision Summary' },
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
    canActivate: [PermitVariationActionGuard],
    canDeactivate: [PermitVariationActionGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitVariationRoutingModule {}
