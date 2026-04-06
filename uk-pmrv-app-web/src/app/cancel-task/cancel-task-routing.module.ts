import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { cancelBacklinkResolver } from './cancel-action.util';
import { CancelComponent, ConfirmationComponent } from './components';

const routes: Routes = [
  {
    path: '',
    component: CancelComponent,
    data: { pageTitle: 'Task cancellation' },
    resolve: {
      backlink: cancelBacklinkResolver,
    },
  },
  {
    path: 'confirmation',
    component: ConfirmationComponent,
    data: { pageTitle: 'Task cancelled' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CancelTaskRoutingModule {}
