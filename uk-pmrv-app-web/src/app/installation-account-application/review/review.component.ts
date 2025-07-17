import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, catchError, combineLatest, iif, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import {
  CompanyInformationService,
  CompanyProfileDTO,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewComponent implements PendingRequest {
  navigationState = { returnUrl: this.router.url };
  readonly info$: Observable<RequestTaskItemDTO> = this.route.data.pipe(map((data) => data.review));
  readonly formSubmission$ = new BehaviorSubject<FormSubmission>(null);
  errorMessage$ = new BehaviorSubject<string>(null);

  actions$: Observable<(RequestActionInfoDTO & { payload?: any; expanded?: boolean })[]> = this.info$.pipe(
    switchMap((info) => this.requestActionsService.getRequestActionsByRequestId(info.requestInfo.id)),
    map((res) => this.sortTimeline(res)),
  );

  allowedRequestTaskActions$ = this.info$.pipe(map((info) => info.allowedRequestTaskActions));

  isAssignableAndCapableToAssign$ = this.info$.pipe(
    map((info) => info.requestTask.assignable && info.userAssignCapable),
  );

  hasRelatedActions$ = combineLatest([this.isAssignableAndCapableToAssign$, this.allowedRequestTaskActions$]).pipe(
    map(
      ([isAssignableAndCapableToAssign, allowedRequestTaskActions]) =>
        isAssignableAndCapableToAssign || hasRequestTaskAllowedActions(allowedRequestTaskActions),
    ),
  );

  taskId$ = this.info$.pipe(map((info) => info.requestTask.id));

  userRoleType$ = this.authStore.pipe(selectUserRoleType, takeUntil(this.destroy$));

  companiesHouse$: Observable<CompanyProfileDTO> = combineLatest([this.info$, this.userRoleType$]).pipe(
    switchMap(([info, roleType]) =>
      iif(
        () => roleType === 'REGULATOR',
        this.companyInformationService.getCompanyProfileByRegistrationNumber(
          (info.requestTask?.payload as any).legalEntity.referenceNumber,
        ),
        of({}) as any,
      ),
    ),
    catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
      this.errorMessage$.next('The registration number was not found at the Companies House');
      return of({});
    }),
    catchError(() => {
      this.errorMessage$.next('The Companies House service is unavailable at the moment. Try again later');
      return of({});
    }),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly requestActionsService: RequestActionsService,
    private readonly backService: BackLinkService,
    private readonly companyInformationService: CompanyInformationService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {
    if (this.router.getCurrentNavigation()?.extras.state) {
      this.backService.show();
    }
  }

  submittedDecision(isAccepted: boolean): void {
    this.formSubmission$.next({ isAccepted, form: 'decision' });
  }

  archived(): void {
    this.router.navigate(['/dashboard']);
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

type FormSubmission = { isAccepted: boolean; form: 'decision' };
