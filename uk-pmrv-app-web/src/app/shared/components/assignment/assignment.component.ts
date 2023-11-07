import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, combineLatest, iif, map, mergeMap, Observable, of, switchMap, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore, selectUserState, UserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { taskNotFoundError } from '@shared/errors/request-task-error';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { RequestTaskItemDTO, TasksAssignmentService, TasksReleaseService } from 'pmrv-api';

import { UserFullNamePipe } from '../../pipes/user-full-name.pipe';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
})
export class AssignmentComponent implements OnInit, OnChanges {
  @Input() info: RequestTaskItemDTO;
  @Input() noBorder: boolean;

  @Output() readonly submitted = new EventEmitter<string>();

  private readonly UNASSIGNED_VALUE = 'unassigned_dummy_value'; // shouldn't be a uuid (uuid represent user ids)

  info$ = new BehaviorSubject<RequestTaskItemDTO>(null);
  options$: Observable<GovukSelectOption<string>[]>;
  assignForm: UntypedFormGroup = this.fb.group({
    assignee: [
      null,
      {
        validators: [GovukValidators.required('Select a person')],
      },
    ],
  });
  isErrorSummaryDisplayed = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly authStore: AuthStore,
    private readonly userFullNamePipe: UserFullNamePipe,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly tasksReleaseService: TasksReleaseService,
    private readonly pendingRequestService: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  ngOnChanges(changes): void {
    if (changes.info) {
      this.info$.next(changes.info.currentValue);
    }
  }

  ngOnInit(): void {
    this.options$ = this.info$.pipe(
      mergeMap((info) => {
        return combineLatest([
          this.authStore.pipe(selectUserState),
          this.tasksAssignmentService.getCandidateAssigneesByTaskId(info.requestTask.id),
          of(info),
        ]);
      }),
      map(([userState, candidates, info]) => [
        ...(!!info.requestTask.assigneeUserId && this.allowReleaseTask(userState)
          ? [{ text: 'Unassigned', value: this.UNASSIGNED_VALUE }]
          : []),
        ...candidates
          .filter((candidates) => candidates.id !== info.requestTask?.assigneeUserId)
          .map((candidate) => ({
            text: this.userFullNamePipe.transform(candidate),
            value: candidate.id,
          })),
      ]),
    );
  }

  submit(taskId: number, userId: string): void {
    if (!this.assignForm.valid) {
      this.isErrorSummaryDisplayed.next(true);
    } else {
      iif(
        () => userId !== this.UNASSIGNED_VALUE,
        this.tasksAssignmentService.assignTask({ taskId, userId }),
        this.tasksReleaseService.releaseTask(taskId),
      )
        .pipe(
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          this.pendingRequestService.trackRequest(),
          switchMap(() => this.options$),
          take(1),
        )
        .subscribe((users) => this.submitted.emit(userId ? users.find((user) => user.value === userId).text : null));
    }
  }

  private allowReleaseTask(userState: UserState) {
    return userState.roleType !== 'OPERATOR';
  }
}
