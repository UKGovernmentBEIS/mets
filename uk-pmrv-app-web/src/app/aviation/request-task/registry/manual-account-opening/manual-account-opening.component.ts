import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAccountViewService,
  EmpIssuanceIndividualCompanyDetails,
  EmpIssuanceLimitedCompanyDetails,
  EmpIssuanceOperatorDetails,
  EmpIssuanceOrganisationDetails,
  EmpIssuancePartnershipDetails,
  RequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { OperatorDetailsLegalStatusTypePipe } from '../../../shared/pipes/operator-details-legal-status-type.pipe';

interface ViewModel {
  header: string;
  operatorDetails: EmpIssuanceOperatorDetails;
  organizationDetails: Partial<
    EmpIssuanceOrganisationDetails &
      EmpIssuanceIndividualCompanyDetails &
      EmpIssuanceLimitedCompanyDetails &
      EmpIssuancePartnershipDetails
  >;
}

@Component({
  selector: 'app-manual-account-opening',
  standalone: true,
  imports: [NgIf, PipesModule, SharedModule, OperatorDetailsLegalStatusTypePipe, ReturnToLinkComponent],
  templateUrl: './manual-account-opening.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManualAccountOpeningComponent implements OnInit {
  aviationAccountViewForRegistry$ = new BehaviorSubject<any>(null);

  ngOnInit(): void {
    this.store
      .pipe(
        requestTaskQuery.selectRequestTaskItem,
        first(),
        switchMap((requestTask) => {
          return this.aviationAcountView.getAviationAccountViewForRegistry(requestTask.requestInfo.id);
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((response: any) => {
        this.aviationAccountViewForRegistry$.next(response);
      });
  }

  vm$: Observable<ViewModel> = combineLatest([this.aviationAccountViewForRegistry$]).pipe(
    map(([aviationAccountView]) => {
      return {
        header: 'Send information to the registry',
        operatorDetails: aviationAccountView?.operatorDetails,
        organizationDetails: aviationAccountView?.organisationDetails,
      };
    }),
  );

  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    protected readonly store: RequestTaskStore,
    readonly pendingRequest: PendingRequestService,
    private readonly aviationAcountView: AviationAccountViewService,
    private router: Router,
    private route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private businessErrorService: BusinessErrorService,
  ) {}

  onSubmit() {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'EMP_ISSUANCE_UKETS_MANUAL_ACCOUNT_OPENING_REGISTRY',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}
