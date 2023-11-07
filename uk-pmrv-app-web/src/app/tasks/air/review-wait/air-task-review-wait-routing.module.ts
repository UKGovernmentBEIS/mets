import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ReviewWaitComponent } from '@tasks/air/review-wait/review-wait.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Review annual improvement report' },
    component: ReviewWaitComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AirTaskReviewWaitRoutingModule {}
