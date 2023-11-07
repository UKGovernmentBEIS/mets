import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { CompletedComponent } from './completed/completed.component';

const routes: Route[] = [
  {
    path: 'completed',
    data: { pageTitle: 'Batch variation completed' },
    component: CompletedComponent,
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReissueRoutingModule {}
