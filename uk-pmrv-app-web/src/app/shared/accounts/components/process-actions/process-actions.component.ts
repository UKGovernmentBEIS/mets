import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap, withLatestFrom } from 'rxjs';

import { AuthStore, selectUserRoleType, UserState } from '@core/store/auth';
import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';

import {
  AccountStatus,
  AviationAccountDTO,
  InstallationAccountDTO,
  RequestCreateActionProcessDTO,
  RequestCreateValidationResult,
  RequestItemsService,
  RequestsService,
} from 'pmrv-api';

import { workflowDetailsTypesMap } from '../../../../workflow-item/shared/workflowDetailsTypesMap';
import { WorkflowLabel, WorkflowMap } from './process-actions-map';

@Component({
  selector: 'app-process-actions',
  templateUrl: './process-actions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessActionsComponent implements OnInit {
  accountId$: Observable<number>;
  availableTasks$: Observable<WorkflowLabel[]>;

  private readonly variationWorkflow: Partial<WorkflowLabel> = {
    title: 'Make a permanent change to your permit plan related to emissions, emission equipment or legal changes',
    button: 'Start a variation',
  };

  private operatorsWorkflowMessagesMap: Partial<WorkflowMap> = {
    PERMIT_SURRENDER: {
      title: 'Surrender your permit and close this installation',
      button: 'Start a permit surrender',
    },
    PERMIT_VARIATION: this.variationWorkflow,
    PERMIT_TRANSFER_A: {
      title: 'Transfer all or part of a permit to another operator',
      button: 'Start a transfer',
    },
    PERMIT_ISSUANCE: undefined,
    PERMIT_NOTIFICATION: {
      title: 'Notify the regulator of a temporary or minor change to your permit plan',
      button: 'Start a notification',
    },
    AER: undefined,
    EMP_VARIATION_UKETS: {
      title: 'Make a change to your emissions plan',
      button: 'Start an emission plan variation',
    },
    EMP_VARIATION_CORSIA: {
      title: 'Make a change to the emissions plan',
      button: 'Start an emission plan variation',
    },
  };

  private regulatorsWorkflowMessagesMap: Partial<WorkflowMap> = {
    PERMIT_VARIATION: this.variationWorkflow,
    PERMIT_REVOCATION: {
      title: 'Revoke your permit',
      button: 'Start a permit revocation',
    },
    NON_COMPLIANCE: {
      title: 'Start a non-compliance task',
      button: 'Start non-compliance',
    },
    AVIATION_ACCOUNT_CLOSURE: {
      title: 'Close this account',
      button: 'Start to close this account',
    },
    AIR: {
      title: 'Trigger annual improvement report',
      button: 'Trigger annual improvement report',
    },
    DOAL: {
      title: 'Start a determination of activity level change',
      button: 'Start determination of activity level',
    },
    WITHHOLDING_OF_ALLOWANCES: {
      title: 'Withhold allowances',
      button: 'Start a withholding of allowances',
    },
    RETURN_OF_ALLOWANCES: {
      title: 'Start return of allowances',
      button: 'Start return of allowances',
    },
    EMP_VARIATION_UKETS: {
      title: 'Make a change to the emissions plan',
      button: 'Start an emission plan variation',
    },
    AVIATION_NON_COMPLIANCE: {
      title: 'Start a non-compliance task',
      button: 'Start non-compliance',
    },
    EMP_VARIATION_CORSIA: {
      title: 'Make a change to the emissions plan',
      button: 'Start an emission plan variation',
    },
  };

  private userRoleWorkflowsMap: Record<UserState['roleType'], Partial<WorkflowMap>> = {
    OPERATOR: this.operatorsWorkflowMessagesMap,
    REGULATOR: this.regulatorsWorkflowMessagesMap,
    VERIFIER: undefined,
  };

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly authStore: AuthStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly itemLinkPipe: ItemLinkPipe,
  ) {}

  ngOnInit(): void {
    this.accountId$ = this.activatedRoute.paramMap.pipe(map((parameters) => +parameters.get('accountId')));

    // request for available tasks
    this.availableTasks$ = this.accountId$.pipe(
      switchMap((accountId) => this.requestsService.getAvailableAccountWorkflows(accountId)),
      withLatestFrom(
        this.authStore.pipe(
          selectUserRoleType,
          map((roleType) => this.userRoleWorkflowsMap[roleType]),
        ),
      ),
      map(([validationResults, userRoleWorkflowMessagesMap]) =>
        Object.entries(validationResults)
          .filter(([type]) => userRoleWorkflowMessagesMap[type])
          .map(([type, result]) => ({
            ...userRoleWorkflowMessagesMap[type],
            type: type,
            errors: result.valid
              ? undefined
              : this.createErrorMessages(type as RequestCreateActionProcessDTO['requestCreateActionType'], result),
          })),
      ),
    );
  }

  onRequestButtonClick(requestType: RequestCreateActionProcessDTO['requestCreateActionType']) {
    if (requestType === 'AIR') {
      this.router.navigate(['../trigger-air'], { relativeTo: this.route }).then();
    } else if (requestType === 'DOAL') {
      this.router.navigate(['../trigger-doal'], { relativeTo: this.route }).then();
    } else {
      this.accountId$
        .pipe(
          switchMap((accountId) =>
            this.requestsService.processRequestCreateAction(
              {
                requestCreateActionType: requestType,
                requestCreateActionPayload: {
                  payloadType: 'EMPTY_PAYLOAD',
                },
              },
              accountId,
            ),
          ),
          switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
          first(),
        )
        .subscribe(({ items }) => {
          const link = items?.length == 1 ? this.itemLinkPipe.transform(items[0]) : ['/dashboard'];
          this.router.navigate(link).then();
        });
    }
  }

  private createErrorMessages(
    requestType: RequestCreateActionProcessDTO['requestCreateActionType'],
    result: RequestCreateValidationResult,
  ): string[] {
    const status = result?.accountStatus as unknown as InstallationAccountDTO['status'] | AviationAccountDTO['status'];
    const typeString = this.getTransformedRequestTypeFragment(requestType);
    if (status && !result?.applicableAccountStatuses?.includes(status as AccountStatus)) {
      const accountStatusString = new AccountStatusPipe().transform(status)?.toUpperCase();

      return [`You cannot start ${typeString} while the account status is ${accountStatusString}.`];
    } else {
      return result?.requests?.length > 0
        ? [
            ...result.requests.map((r) =>
              this.createErrorMessage(requestType, r as RequestCreateActionProcessDTO['requestCreateActionType']),
            ),
          ]
        : (result as any)?.improvementsExist === false
        ? [`You cannot trigger ${typeString} as the installation does not have any improvements to make.`]
        : ['Action currently unavailable'];
    }
  }

  private createErrorMessage(
    currentRequestType: RequestCreateActionProcessDTO['requestCreateActionType'],
    resultRequestType: RequestCreateActionProcessDTO['requestCreateActionType'],
  ): string {
    const currentRequestTypeString = this.getTransformedRequestTypeFragment(currentRequestType);
    const resultRequestTypeString = this.getTransformedRequestTypeFragment(resultRequestType);

    if (currentRequestType === resultRequestType) {
      return `You cannot start ${currentRequestTypeString} as there is already one in progress.`;
    } else {
      return `You cannot start ${currentRequestTypeString} while ${resultRequestTypeString} is in progress.`;
    }
  }

  private getTransformedRequestTypeFragment(requestType: RequestCreateActionProcessDTO['requestCreateActionType']) {
    return requestType === 'AIR'
      ? `an ${workflowDetailsTypesMap[requestType].toLowerCase()}`
      : `a ${workflowDetailsTypesMap[requestType].toLowerCase()}`;
  }
}
