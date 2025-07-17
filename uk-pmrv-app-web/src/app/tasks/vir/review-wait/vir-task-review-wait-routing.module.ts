import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ReviewWaitComponent } from '@tasks/vir/review-wait/review-wait.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review verifier improvement report' },
        component: ReviewWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VirTaskReviewWaitRoutingModule {}
