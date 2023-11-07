import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { BatchReissueRequestCreateActionPayload, RegulatorUsersAuthoritiesInfoDTO, RequestsService } from 'pmrv-api';

import {
  anotherInProgressError,
  noMatchingEmittersError,
} from '../../../shared/components/batch-reissue-submit/errors/business-errors';
import { FiltersModel } from '../../../shared/components/permit-batch-reissue/filters.model';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  state$ = this.store.state$;
  filters$ = this.state$.pipe(
    map((state) => {
      return {
        accountStatuses: state.accountStatuses,
        emitterTypes: state.emitterTypes,
        installationCategories: state.installationCategories,
        numberOfEmitters: null,
      } as FiltersModel;
    }),
  );
  regulators$ = this.route.data.pipe(map((data: { regulators: RegulatorUsersAuthoritiesInfoDTO }) => data.regulators));

  isFormSubmitted$ = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  signatoryName$ = combineLatest([this.state$, this.regulators$]).pipe(
    map(([state, regulators]) => {
      const selected = regulators.caUsers.find((regulator) => regulator.userId === state.signatory);
      return `${selected?.firstName} ${selected?.lastName}`;
    }),
  );

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly store: PermitBatchReissueStore,
    private readonly requestsService: RequestsService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.requestsService.processRequestCreateAction({
            requestCreateActionType: 'PERMIT_BATCH_REISSUE',
            requestCreateActionPayload: {
              payloadType: 'PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD',
              filters: {
                accountStatuses: state.accountStatuses,
                emitterTypes: state.emitterTypes,
                installationCategories: state.installationCategories,
              },
              signatory: state.signatory,
            } as BatchReissueRequestCreateActionPayload,
          }),
        ),
        catchBadRequest(ErrorCodes.BATCHREISSUE0001, () =>
          this.businessErrorService.showError(anotherInProgressError(false)),
        ),
        catchBadRequest(ErrorCodes.BATCHREISSUE0002, () =>
          this.businessErrorService.showError(noMatchingEmittersError(false)),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((response) => {
        this.requestId$.next(response.requestId);
        this.isFormSubmitted$.next(true);
      });
  }
}
