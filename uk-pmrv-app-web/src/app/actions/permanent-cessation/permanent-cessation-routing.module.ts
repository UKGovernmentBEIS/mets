import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { peerReviewTimelineRoutes } from '@shared/components/peer-review-decision/peer-review-decision.routes';

import { PermanentCessationActionSubmittedComponent } from './submitted/submitted.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
  {
    path: 'submitted',
    data: { pageTitle: 'Permanent cessation submitted' },
    component: PermanentCessationActionSubmittedComponent,
  },
  ...peerReviewTimelineRoutes,
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermanentCessationRoutingModule {}
