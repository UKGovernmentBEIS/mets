import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ChangeAssigneeComponent } from './components';

const routes: Routes = [
  {
    path: '',
    component: ChangeAssigneeComponent,
    data: { backlink: '../' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChangeTaskAssigneeRoutingModule {}
