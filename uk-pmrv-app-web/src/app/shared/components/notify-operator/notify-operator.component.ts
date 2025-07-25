import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  take,
  takeUntil,
  tap,
} from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore, selectCurrentDomain, selectUserState } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { templateError } from '@shared/errors/template-error';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukSelectOption } from 'govuk-components';

import {
  CaExternalContactDTO,
  CaExternalContactsService,
  OperatorAuthoritiesService,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksAssignmentService,
  TasksService,
} from 'pmrv-api';

import {
  catchBadRequest,
  catchTaskReassignedBadRequest,
  ErrorCodes as BusinessErrorCode,
} from '../../../error/business-errors';
import {
  externalContactsToNotifyHeaderMapper,
  NotifyAccountOperatorUsersInfo,
  notifyAccountOperatorUsersInfoReduceCallback,
  returnToTextMapper,
  toAccountOperatorUser,
  usersToNotifyHeaderMapper,
} from './notify-operator';
import { NOTIFY_OPERATOR_FORM, notifyOperatorFormFactory } from './notify-operator-form.provider';

@Component({
  selector: 'app-notify-operator',
  templateUrl: './notify-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [notifyOperatorFormFactory, UserFullNamePipe],
})
export class NotifyOperatorComponent implements PendingRequest, OnInit {
  @Input() taskId: number;
  @Input() accountId: number;
  @Input() requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  @Input() pendingRfi: boolean;
  @Input() pendingRde: boolean;
  @Input() confirmationMessage: string;
  @Input() confirmationText: string;
  @Input() referenceCode: string;
  @Input() isRegistryToBeNotified = false;
  @Input() decisionType: string;
  @Input() issueNoticeOfIntent?: boolean;
  @Input() hasSignature = true;
  @Input() serviceContactAutomaticallyNotified = true;
  @Input() previewDocuments: DocumentFilenameAndDocumentType[];

  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  errorMessage$ = new BehaviorSubject<string>(null);
  isFormSubmitted$ = new BehaviorSubject(false);
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  objectKeys = Object.keys;

  accountOperatorUsersAutomaticallyNotified$: Observable<NotifyAccountOperatorUsersInfo>;
  accountOperatorUsersOther$: Observable<NotifyAccountOperatorUsersInfo>;
  externalContacts$: Observable<Array<CaExternalContactDTO>>;
  assignees$: Observable<GovukSelectOption<string>[]>;

  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  isAviation$ = this.currentDomain$.pipe(map((currentDomain) => currentDomain === 'AVIATION'));
  usersToNotify$ = this.currentDomain$.pipe(
    map((currentDomain) => usersToNotifyHeaderMapper(currentDomain, this.requestTaskActionType)),
  );
  externalContactsToNotify$ = this.currentDomain$.pipe(
    map((currentDomain) => externalContactsToNotifyHeaderMapper(currentDomain, this.requestTaskActionType)),
  );
  returnToTextMapper: string;

  constructor(
    @Inject(NOTIFY_OPERATOR_FORM) readonly form: UntypedFormGroup,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly externalContactsService: CaExternalContactsService,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly tasksService: TasksService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbsService: BreadcrumbService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.returnToTextMapper = returnToTextMapper(this.requestTaskActionType);
    const accountOperatorAuthorities$ = this.operatorAuthoritiesService
      .getAccountOperatorAuthorities(this.accountId)
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    const accountOperatorActiveUsersWithAuthorities$ = combineLatest([
      accountOperatorAuthorities$.pipe(map((accountOperator) => accountOperator.authorities)),
      accountOperatorAuthorities$.pipe(map((accountOperator) => accountOperator.contactTypes)),
    ]).pipe(
      map(([authorities, contactTypes]) =>
        authorities
          .filter((authority) => authority.authorityStatus === 'ACTIVE')
          .map((authority) => toAccountOperatorUser(authority, contactTypes)),
      ),
    );

    this.accountOperatorUsersAutomaticallyNotified$ = accountOperatorActiveUsersWithAuthorities$.pipe(
      map((users) =>
        users
          .filter(
            (user) =>
              user.contactTypes.includes('PRIMARY') ||
              (user.contactTypes.includes('SERVICE') && this.serviceContactAutomaticallyNotified),
          )
          .reduce(notifyAccountOperatorUsersInfoReduceCallback, {}),
      ),
    );

    this.accountOperatorUsersOther$ = accountOperatorActiveUsersWithAuthorities$.pipe(
      map((users) =>
        users
          .filter((user) =>
            this.serviceContactAutomaticallyNotified
              ? !user.contactTypes.includes('PRIMARY') && !user.contactTypes.includes('SERVICE')
              : !user.contactTypes.includes('PRIMARY'),
          )
          .reduce(notifyAccountOperatorUsersInfoReduceCallback, {}),
      ),
    );

    this.externalContacts$ = this.externalContactsService.getCaExternalContacts().pipe(
      map((contacts) => contacts.caExternalContacts),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.assignees$ = of(this.taskId).pipe(
      switchMap((taskId: number) => this.tasksAssignmentService.getCandidateAssigneesByTaskId(taskId)),
      map((assignees) =>
        assignees.map((assignee) => ({ text: this.fullNamePipe.transform(assignee), value: assignee.id })),
      ),
      tap((assignees) => {
        this._populateAssigneeDropDown(assignees);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
    );
  }

  returnToUrl(
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    domain?: AccountType,
  ): string {
    switch (domain) {
      case 'INSTALLATION':
        switch (requestTaskActionType) {
          case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
            return '/dashboard';

          default:
            return '..';
        }

      case 'AVIATION':
        switch (requestTaskActionType) {
          case 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
          case 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
          case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
          case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
          case 'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR':
          case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR':
          case 'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR':
          case 'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR':
          case 'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION':
          case 'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR':
            return '../../../';

          case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
          case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
            return '../../../../';

          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
            return '../../';

          default:
            return '..';
        }
    }
  }

  get decisionNotification() {
    return {
      operators: this.form.get('users').value,
      externalContacts: this.form.get('contacts').value,
      ...(this.hasSignature && { signatory: this.form.get('assignees').value }),
    };
  }

  onSubmit(): void {
    if (this.form.valid || !this.hasSignature) {
      const payloadType = this.getPayloadType();

      of(this.taskId)
        .pipe(
          switchMap((taskId) =>
            this.tasksService.processRequestTaskAction({
              requestTaskActionType: this.requestTaskActionType,
              requestTaskId: taskId,
              requestTaskActionPayload: {
                payloadType,
                decisionNotification: {
                  operators: this.form.get('users').value,
                  externalContacts: this.form.get('contacts').value,
                  ...(this.hasSignature && { signatory: this.form.get('assignees').value }),
                },
              } as RequestTaskActionPayload,
            }),
          ),
          this.pendingRequest.trackRequest(),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchBadRequest(
            [
              BusinessErrorCode.NOTIF1000,
              BusinessErrorCode.NOTIF1001,
              BusinessErrorCode.NOTIF1002,
              BusinessErrorCode.NOTIF1003,
              BusinessErrorCode.ACCOUNT1001,
            ],
            (res) => {
              return templateError(res, this.isTemplateGenerationErrorDisplayed$, this.errorMessage$);
            },
          ),
          catchTaskReassignedBadRequest(() =>
            this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
        )
        .subscribe(() => {
          this.breadcrumbsService.showDashboardBreadcrumb(this.router.url);
          this.isFormSubmitted$.next(true);
        });
    } else {
      this.isSummaryDisplayed.next(true);
    }
  }

  private getPayloadType(): RequestTaskActionPayload['payloadType'] {
    let payloadType: RequestTaskActionPayload['payloadType'];

    switch (this.requestTaskActionType) {
      case 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
        payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD';
        break;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD';
        break;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
        payloadType = 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION_PAYLOAD';
        break;
      case 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
        payloadType = 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD';
        break;

      case 'DRE_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'DRE_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;

      case 'DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'DOAL_SUBMIT_APPLICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'VIR_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'AIR_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'AIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
        payloadType = 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD';
        break;
      case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
        payloadType = 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD';
        break;

      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR':
        payloadType = 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR_PAYLOAD';
        break;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR':
        payloadType = 'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR_PAYLOAD';
        break;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR':
        payloadType = 'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR_PAYLOAD';
        break;

      case 'RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;
      case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;
      case 'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;
      case 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;

      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;

      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;

      case 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION':
        payloadType = 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD';
        break;

      case 'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR':
        payloadType = 'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR_PAYLOAD';
        break;
    }

    return payloadType;
  }

  private _populateAssigneeDropDown(
    assignees: {
      text: string;
      value: string;
    }[],
  ) {
    this.authStore
      .pipe(selectUserState)
      .pipe(
        take(1),
        map((userState) => userState.userId),
      )
      .subscribe((userId) => {
        const res = assignees.find((a) => a.value === userId);

        if (res) {
          this.form.get('assignees').setValue(res.value);
        }
      });
  }
}
