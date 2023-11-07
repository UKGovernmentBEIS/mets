import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RespondedComponent } from './responded.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Follow up response' },
    component: RespondedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AirActionRespondedRoutingModule {}
