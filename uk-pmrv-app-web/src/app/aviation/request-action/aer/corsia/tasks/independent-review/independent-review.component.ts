import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { IndependentReviewCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/independent-review-corsia-template/independent-review-corsia-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaIndependentReview, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaIndependentReview;
}

@Component({
  selector: 'app-independent-review',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink, IndependentReviewCorsiaTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-independent-review-corsia-template [data]="vm.data"></app-independent-review-corsia-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class IndependentReviewComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['independentReview'],
      data: verificationReport.independentReview,
    })),
  );
}
