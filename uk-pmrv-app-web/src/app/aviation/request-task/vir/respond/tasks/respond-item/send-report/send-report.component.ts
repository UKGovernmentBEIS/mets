import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

interface ViewModel {
  heading: string;
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report',
  standalone: true,
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, RouterLink],
})
export class SendReportComponent {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, isEditable]) => {
      return {
        heading: `Submit response to ${this.verificationDataItem.reference}`,
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private readonly breadcrumbsService: BreadcrumbService,
    private route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.store.virDelegate
      .submitRespondVir(this.verificationDataItem.reference)
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
        this.breadcrumbsService.clear();
      });
  }
}
