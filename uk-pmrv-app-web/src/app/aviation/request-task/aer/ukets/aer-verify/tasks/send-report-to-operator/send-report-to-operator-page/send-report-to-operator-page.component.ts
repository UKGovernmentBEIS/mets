import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { map } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-send-report-to-operator-page',
  templateUrl: './send-report-to-operator-page.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class SendReportToOperatorPageComponent {
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(map((isEditable) => isEditable));

  constructor(
    private readonly store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .submitAerVerify()
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['confirmation-operator'], { relativeTo: this.route, replaceUrl: true }));
  }
}
