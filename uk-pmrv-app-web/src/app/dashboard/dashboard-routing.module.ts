import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardPageComponent } from '@shared/dashboard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'UK Emissions Trading Scheme reporting dashboard' },
    component: DashboardPageComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {}
