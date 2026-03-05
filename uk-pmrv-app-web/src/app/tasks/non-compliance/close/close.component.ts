import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  NonComplianceCloseApplicationRequestTaskActionPayload,
  RequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { NonComplianceService } from '../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../core/non-compliance-form.token';
import { closeFormProvider } from './close-form.provider';

@Component({
  selector: 'app-close',
  templateUrl: './close.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [closeFormProvider],
})
export class CloseComponent implements OnInit, OnDestroy {
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  requestTask$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map((requestTaskItem) => requestTaskItem.requestTask.type),
  );

  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
    private readonly tasksService: TasksService,
    private readonly store: CommonTasksStore,
    private readonly businessErrorService: BusinessErrorService,
    readonly nonComplianceService: NonComplianceService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbsService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.requestTask$.pipe(first()).subscribe((requestTask) => {
      switch (requestTask) {
        case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['daily-penalty-notice', 'peer-review-wait']);
          break;
        case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['daily-penalty-notice', 'dpn-peer-review']);
          break;
        case 'NON_COMPLIANCE_NOTICE_OF_INTENT':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('notice-of-intent');
          break;
        case 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['notice-of-intent', 'peer-review-wait']);
          break;
        case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['notice-of-intent', 'noi-peer-review']);
          break;
        case 'NON_COMPLIANCE_CIVIL_PENALTY':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('civil-penalty-notice');
          break;
        case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['civil-penalty-notice', 'peer-review-wait']);
          break;
        case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
          this.breadcrumbsService.addToLastBreadcrumbAndShow(['civil-penalty-notice', 'cpn-peer-review']);
          break;
        case 'NON_COMPLIANCE_FINAL_DETERMINATION':
          this.breadcrumbsService.addToLastBreadcrumbAndShow('conclusion');
          break;
        default:
          break;
      }
    });
  }

  onSubmit() {
    if (this.form.valid) {
      this.closeNonCompliance(
        {
          reason: this.form.value.reason,
          files: this.form.value.files?.map((file) => file.uuid),
        },
        true,
        {
          ...this.form.value.files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
        },
      )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  getBaseFileDownloadUrl() {
    return this.nonComplianceService.getBaseFileDownloadUrl();
  }

  closeNonCompliance(
    payload: NonComplianceCloseApplicationRequestTaskActionPayload,
    sectionCompleted: boolean,
    //TODO
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'NON_COMPLIANCE_CLOSE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'NON_COMPLIANCE_CLOSE_APPLICATION_PAYLOAD',
              reason: payload.reason,
              files: payload.files,
              sectionCompleted: sectionCompleted,
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
          );
      }),
    );
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
