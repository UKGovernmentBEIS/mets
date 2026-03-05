import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable, Subject, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { reviewWizardComplete } from '@tasks/air/review/review.wizard';
import { AirService } from '@tasks/air/shared/services/air.service';
import { getPreviewDocumentsInfo } from '@tasks/air/shared/utils/previewDocumentsAir.util';

import { AirApplicationReviewRequestTaskPayload, RequestItemsService } from 'pmrv-api';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements OnInit, OnDestroy {
  pendingRfi$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private readonly airService: AirService,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly requestItemsService: RequestItemsService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.airService.requestTaskItem$.pipe(map((data) => data.requestInfo.accountId));
  referenceCode = this.airService.requestId;
  isSendReportAvailable$ = (this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => reviewWizardComplete(payload)),
  );
  isEditable$ = this.airService.isEditable$;
  previewDocuments = getPreviewDocumentsInfo('AIR_NOTIFY_OPERATOR_FOR_DECISION');

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/tasks', paramMap.get('taskId'), 'air', 'review'];
      this.breadcrumbService.show([{ text: 'Annual improvement report review', link }]);
    });

    this.requestItemsService
      .getItemsByRequest(this.referenceCode)
      .pipe(
        first(),
        tap((res) => this.pendingRfi$.next(res.items.some((i) => 'AIR_WAIT_FOR_RFI_RESPONSE' === i.taskType))),
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.breadcrumbService.clear();
  }
}
