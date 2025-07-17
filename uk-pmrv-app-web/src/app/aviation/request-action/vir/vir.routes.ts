import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestActionStore } from '@aviation/request-action/store';
import { DecisionSummaryComponent } from '@aviation/request-action/vir/decision-summary/decision-summary.component';
import { ReferenceItemResolver } from '@aviation/request-action/vir/reference-item.resolver';
import { RecommendationResponseItemComponent } from '@aviation/request-action/vir/reviewed/tasks/recommendation-response-item/recommendation-response-item.component';
import { ReportSummaryComponent } from '@aviation/request-action/vir/reviewed/tasks/report-summary/report-summary.component';
import { ReferenceItemComponent } from '@aviation/request-action/vir/submitted/tasks/reference-item/reference-item.component';

const canDeactivateVir: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

export const VIR_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateVir],
    children: [
      {
        path: 'decision-summary',
        component: DecisionSummaryComponent,
      },
      {
        path: 'submitted',
        children: [
          {
            path: ':id',
            resolve: { verificationDataItem: ReferenceItemResolver },
            component: ReferenceItemComponent,
          },
        ],
      },
      {
        path: 'reviewed',
        children: [
          {
            path: 'report-summary',
            component: ReportSummaryComponent,
          },
          {
            path: ':id',
            resolve: { verificationDataItem: ReferenceItemResolver },
            component: RecommendationResponseItemComponent,
          },
        ],
      },
    ],
  },
];
