import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { BehaviorSubject, first, map, switchMap, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { HSETIRequestCreateActionPayload, RequestItemsService, RequestsService } from 'pmrv-api';

import { ItemLinkPipe } from '../../shared/pipes/item-link.pipe';

@Component({
  selector: 'app-trigger-hse',
  imports: [CommonModule, SharedModule, RouterModule],
  templateUrl: './trigger-hseti.component.html',
  providers: [PendingRequestService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TriggerHseTiComponent implements OnInit {
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  inProgressAllocationPeriod$ = new BehaviorSubject<string[]>([]);

  years = [2021, 2025, 2026, 2030];
  allocationPeriods: string[] = [];

  ngOnInit(): void {
    this.allocationPeriods = [];
    for (let i = 0; i < this.years.length; i += 2) {
      this.allocationPeriods.push(`${this.years[i]}-${this.years[i + 1]}`);
    }

    this.accountId$
      .pipe(
        first(),
        switchMap((accountId) =>
          this.requestsService.getRequestDetailsByResource({
            resourceType: 'ACCOUNT',
            resourceId: String(accountId),
            requestTypes: ['HSE_TI'],
            requestStatuses: ['IN_PROGRESS'],
            category: 'PERMIT',
            pageSize: 2,
            pageNumber: 0,
          }),
        ),
        map((response) =>
          (response?.requestDetails || [])
            .map((metaData) => metaData?.requestMetadata?.['allocationPeriod'])
            .filter(Boolean),
        ),
      )
      .subscribe((periods) => this.inProgressAllocationPeriod$.next(periods));
  }

  form = this.fb.group({
    allocationPeriod: [null, { validators: GovukValidators.required('Select the allocation period') }],
  });

  constructor(
    private fb: FormBuilder,
    private readonly pendingRequest: PendingRequestService,
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly itemLinkPipe: ItemLinkPipe,
  ) {}

  createAllocationPeriod(period): string {
    return `PERIOD_${period.replace('-', '_')}`;
  }

  onSubmit(): void {
    const allocationPeriod: string = this.form.get('allocationPeriod').value;
    if (!this.form.valid) {
      this.isErrorSummaryDisplayed$.next(true);
    } else if (allocationPeriod) {
      this.accountId$
        .pipe(
          first(),
          switchMap((accountId) =>
            this.requestsService.processRequestCreateAction(
              {
                requestCreateActionType: 'HSE_TI',
                requestCreateActionPayload: {
                  payloadType: 'HSE_TI_REQUEST_CREATE_ACTION_PAYLOAD',
                  allocationPeriod: `PERIOD_${allocationPeriod.replace('-', '_')}`,
                } as HSETIRequestCreateActionPayload,
              },
              String(accountId),
            ),
          ),
          switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
          take(1),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(({ items }) => {
          const link = items?.length == 1 ? this.itemLinkPipe.transform(items[0], false) : ['/dashboard'];
          this.router.navigate(link).then();
        });
    }
  }
}
