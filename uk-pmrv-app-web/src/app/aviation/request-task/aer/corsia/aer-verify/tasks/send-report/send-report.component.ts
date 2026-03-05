import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent {
  isSubmitted$ = new BehaviorSubject(false);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, isEditable]) => {
      return {
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    readonly pendingRequestService: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .submitAerVerify()
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
        this.breadcrumbService.clear();
      });
  }
}
