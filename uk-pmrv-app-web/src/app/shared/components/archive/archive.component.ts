import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { first, of, switchMap } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { mapToDismiss } from '../../../installation-account-application/pipes/action-pipes';
import { requestTaskReassignedError, taskNotFoundError } from '../../errors/request-task-error';

@Component({
  selector: 'app-archive',
  template: `
    <govuk-warning-text *ngIf="warningText">
      No actions are currently required.
      <br />
      {{ warningText }}
    </govuk-warning-text>
    <ng-content></ng-content>
    <button (click)="submit()" appPendingButton govukSecondaryButton type="button">
      {{ buttonText }}
    </button>
    <hr class="govuk-!-margin-bottom-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArchiveComponent {
  @Input() type:
    | 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE'
    | 'SYSTEM_MESSAGE_DISMISS'
    | 'INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE';
  @Input() taskId: number;
  @Input() warningText: string;
  @Input() buttonText = 'Archive now and return to dashboard';

  @Output() readonly submitted = new EventEmitter<void>();

  constructor(
    readonly pendingRequestService: PendingRequestService,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  submit(): void {
    of(this.taskId)
      .pipe(
        first(),
        mapToDismiss(this.type),
        switchMap((payload) => this.tasksService.processRequestTaskAction(payload)),
        this.pendingRequestService.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.submitted.emit());
  }
}
