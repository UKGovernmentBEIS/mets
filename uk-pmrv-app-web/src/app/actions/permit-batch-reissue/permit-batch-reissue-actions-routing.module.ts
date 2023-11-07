import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { CompletedComponent } from './completed/completed.component';
import { SubmittedComponent } from './submitted/submitted.component';

const routes: Route[] = [
  {
    path: 'submitted',
    data: { pageTitle: 'Permit batch reissue submitted' },
    component: SubmittedComponent,
  },
  {
    path: 'completed',
    data: { pageTitle: 'Permit batch reissue completed' },
    component: CompletedComponent,
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitBatchReissueRoutingModule {}
