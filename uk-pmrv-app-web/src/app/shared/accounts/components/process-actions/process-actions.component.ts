import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap, take, withLatestFrom } from 'rxjs';

import { AuthStore, selectCurrentDomain, selectUserRoleType, UserState } from '@core/store/auth';
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
import { WorkflowArray, WorkflowLabel, WorkflowLabelProperties } from './process-actions-map';

@Component({
  selector: 'app-process-actions',
  templateUrl: './process-actions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessActionsComponent implements OnInit {
  accountId$: Observable<number>;
  availableTasks$: Observable<WorkflowLabel[]>;

  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, take(1));
  isAviation: boolean;

  private readonly variationWorkflow: WorkflowLabel = {
    title: 'Make a permanent change to your permit plan related to emissions, emission equipment or legal changes',
    properties: [
      {
        button: 'Start a variation',
        type: 'PERMIT_VARIATION',
        errors: [],
      },
    ],
  };

  private operatorsWorkflowMessagesMap: WorkflowArray = [
    {
      title: 'Surrender your permit and close this installation',
      properties: [
        {
          button: 'Start a permit surrender',
          type: 'PERMIT_SURRENDER',
          errors: [],
        },
      ],
    },
    this.variationWorkflow,
    {
      title: 'Transfer all or part of a permit to another operator',
      properties: [
        {
          button: 'Start a transfer',
          type: 'PERMIT_TRANSFER_A',
          errors: [],
        },
      ],
    },

    {
      title: 'Notify the regulator of a change',
      properties: [
        {
          button: 'Start a notification',
          type: 'PERMIT_NOTIFICATION',
          errors: [],
        },
      ],
    },

    {
      title: 'Make a change to your emissions plan',
      properties: [
        {
          button: 'Start an emission plan variation',
          type: 'EMP_VARIATION_UKETS',
          errors: [],
        },
      ],
    },
    {
      title: 'Make a change to the emissions plan',
      properties: [
        {
          button: 'Start an emission plan variation',
          type: 'EMP_VARIATION_CORSIA',
          errors: [],
        },
      ],
    },
  ];

  private regulatorsWorkflowMessagesMap: WorkflowArray = [
    this.variationWorkflow,
    {
      title: 'Revoke your permit',
      properties: [
        {
          button: 'Start a permit revocation',
          type: 'PERMIT_REVOCATION',
          errors: [],
        },
      ],
    },
    {
      title: 'Start a non-compliance task',
      properties: [
        {
          button: 'Start non-compliance',
          type: 'NON_COMPLIANCE',
          errors: [],
        },
      ],
    },
    {
      title: 'Close this account',
      properties: [
        {
          button: 'Start to close this account',
          type: 'AVIATION_ACCOUNT_CLOSURE',
          errors: [],
        },
      ],
    },
    {
      title: 'Trigger annual improvement report',
      properties: [
        {
          button: 'Trigger annual improvement report',
          type: 'AIR',
          errors: [],
        },
      ],
    },
    {
      title: 'Start a determination of activity level change',
      properties: [
        {
          button: 'Start determination of activity level',
          type: 'DOAL',
          errors: [],
        },
      ],
    },
    {
      title: 'Withhold allowances',
      properties: [
        {
          button: 'Start a withholding of allowances',
          type: 'WITHHOLDING_OF_ALLOWANCES',
          errors: [],
        },
      ],
    },
    {
      title: 'Start return of allowances',
      properties: [
        {
          button: 'Start return of allowances',
          type: 'RETURN_OF_ALLOWANCES',
          errors: [],
        },
      ],
    },
    {
      title: 'Make a change to the emissions plan',
      properties: [
        {
          button: 'Start an emission plan variation',
          type: 'EMP_VARIATION_UKETS',
          errors: [],
        },
      ],
    },
    {
      title: 'Start a non-compliance task',
      properties: [
        {
          button: 'Start non-compliance',
          type: 'AVIATION_NON_COMPLIANCE',
          errors: [],
        },
      ],
    },
    {
      title: 'Make a change to the emissions plan',
      properties: [
        {
          button: 'Start an emission plan variation',
          type: 'EMP_VARIATION_CORSIA',
          errors: [],
        },
      ],
    },
    {
      title: 'Start a new inspection',
      properties: [
        {
          button: 'Start an on-site inspection',
          type: 'INSTALLATION_ONSITE_INSPECTION',
          errors: [],
        },
        {
          button: 'Start an audit report',
          type: 'INSTALLATION_AUDIT',
          errors: [],
        },
      ],
    },
    {
      title: 'Permanent cessation',
      properties: [
        {
          button: 'Start a permanent cessation',
          type: 'PERMANENT_CESSATION',
          errors: [],
        },
      ],
    },
  ];

  private userRoleWorkflowsMap: Record<UserState['roleType'], WorkflowArray> = {
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
    this.currentDomain$.subscribe((domain) => {
      this.isAviation = domain === 'AVIATION';
    });

    this.accountId$ = this.activatedRoute.paramMap.pipe(map((parameters) => +parameters.get('accountId')));

    this.availableTasks$ = this.accountId$.pipe(
      switchMap((accountId) => this.requestsService.getAvailableAccountWorkflows(accountId)),
      withLatestFrom(
        this.authStore.pipe(
          selectUserRoleType,
          map((roleType) => this.userRoleWorkflowsMap[roleType]),
        ),
      ),
      map(([validationResults, userRoleWorkflowMessagesMap]) => {
        const allowedWorkflowTypes = Object.keys(validationResults);
        return userRoleWorkflowMessagesMap
          .filter((workflow) => {
            const currentWorkflowTypes = workflow.properties.map((property) => property.type);

            return allowedWorkflowTypes.some(
              (allowedWorkflowType: RequestCreateActionProcessDTO['requestCreateActionType']) =>
                currentWorkflowTypes.includes(allowedWorkflowType),
            );
          })
          .map((workflow) => {
            const properties = (workflow.properties as WorkflowLabelProperties[])
              .filter((property) => allowedWorkflowTypes.includes(property.type))
              .map((property) => ({
                button: property.button,
                type: property.type,
                errors: validationResults[property.type].valid
                  ? []
                  : this.createErrorMessages(property.type, validationResults[property.type]),
              }));

            return {
              title: workflow.title,
              properties,
            };
          });
      }),
    );
  }

  onRequestButtonClick(requestType: RequestCreateActionProcessDTO['requestCreateActionType']) {
    if (requestType === 'AIR') {
      this.router.navigate(['../trigger-air'], { relativeTo: this.route }).then();
    } else if (requestType === 'DOAL') {
      this.router.navigate(['../trigger-doal'], { relativeTo: this.route }).then();
    } else if (requestType === 'INSTALLATION_AUDIT') {
      this.router.navigate(['../audit-year'], { relativeTo: this.route }).then();
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
              String(accountId),
            ),
          ),
          switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
          first(),
        )
        .subscribe(({ items }) => {
          const link = items?.length == 1 ? this.itemLinkPipe.transform(items[0], this.isAviation) : ['/dashboard'];
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
    const result = workflowDetailsTypesMap[requestType].toLowerCase();

    return ['AIR', 'INSTALLATION_ONSITE_INSPECTION', 'INSTALLATION_AUDIT'].includes(requestType)
      ? `an ${result}`
      : `a ${result}`;
  }
}
