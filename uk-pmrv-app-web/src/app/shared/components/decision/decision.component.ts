import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, catchError, EMPTY, first, of, switchMap, throwError } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { mapToDecision } from '../../../installation-account-application/pipes/action-pipes';
import { requestTaskReassignedError, taskNotFoundError } from '../../errors/request-task-error';
import { UserFullNamePipe } from '../../pipes/user-full-name.pipe';

@Component({
  selector: 'app-decision',
  templateUrl: './decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
})
export class DecisionComponent {
  @Input() taskId: number;
  @Output() readonly submitted = new EventEmitter<boolean>();

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  decisionForm: UntypedFormGroup = this.fb.group({
    isAccepted: [null, GovukValidators.required('You need to approve or reject the application')],
    acceptanceReason: [
      null,
      [
        GovukValidators.required('Explain why you are approving this application'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ],
    ],
    rejectionReason: [
      null,
      [
        GovukValidators.required('Enter the reason for rejection'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ],
    ],
  });

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly tasksService: TasksService,
    private readonly pendingRequestService: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  submit(isAccepted: boolean): void {
    if (this.decisionForm.valid) {
      of(this.taskId)
        .pipe(
          first(),
          mapToDecision(isAccepted, this.decisionForm.get(isAccepted ? 'acceptanceReason' : 'rejectionReason').value),
          switchMap((payload) => this.tasksService.processRequestTaskAction(payload)),
          this.pendingRequestService.trackRequest(),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchTaskReassignedBadRequest(() =>
            this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
          catchError((res) => {
            switch (res?.error?.code) {
              case 'ACCOUNT1002':
                this.decisionForm.get('isAccepted').setErrors({
                  legalEntityExists:
                    'The legal entity name already exists. Update legal entity by selecting current legal entity from the legal entity list or enter a new legal entity.',
                });
                break;
              default:
                return throwError(() => res);
            }
            this.isSummaryDisplayed$.next(true);
            return EMPTY;
          }),
        )
        .subscribe(() => this.submitted.emit(isAccepted));
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
