import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DreService } from '@tasks/dre/core/dre.service';
import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { Dre } from 'pmrv-api';

import { summaryFormFactory } from './summary-form.provider';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [summaryFormFactory],
})
export class SummaryComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  dre$: Observable<Dre> = this.dreService.dre$;

  displayErrorSummary$ = new BehaviorSubject(false);

  isTaskSubmitted$ = this.dreService.payload$.pipe(
    first(),
    map(
      (payload) =>
        !(
          payload.payloadType === 'DRE_WAIT_FOR_PEER_REVIEW_PAYLOAD' ||
          payload.payloadType === 'DRE_APPLICATION_PEER_REVIEW_PAYLOAD'
        ),
    ),
  );

  requestTask$ = this.dreService.requestTaskItem$.pipe(
    first(),
    map((requestTaskItem) => requestTaskItem.requestTask),
  );

  returnTo: { text: string; link: string };

  constructor(
    @Inject(DRE_TASK_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly dreService: DreService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  get supportingDocumentFiles$() {
    return this.dre$.pipe(
      map((dre) => this.dreService.getDownloadUrlFiles(dre.determinationReason?.supportingDocuments)),
    );
  }

  ngOnInit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }

    combineLatest([this.isTaskSubmitted$, this.requestTask$])
      .pipe(
        switchMap(async ([isTaskSubmitted, requestTask]) => {
          if (!isTaskSubmitted) {
            const taskId = requestTask?.id;
            const text = 'Reportable emissions';
            let link;
            switch (requestTask.payload.payloadType) {
              case 'DRE_WAIT_FOR_PEER_REVIEW_PAYLOAD':
                link = ['/tasks', taskId, 'dre', 'peer-review-wait'];
                break;
              case 'DRE_APPLICATION_PEER_REVIEW_PAYLOAD':
                link = ['/tasks', taskId, 'dre', 'peer-review'];
                break;
              default:
                link = ['/tasks', taskId, 'dre', 'submit'];
                break;
            }
            this.returnTo = { text, link };
          } else {
            const text = 'reportable emissions task';
            const link = '..';
            this.returnTo = { text, link };
          }
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.dreService
      .saveSectionStatus(true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
