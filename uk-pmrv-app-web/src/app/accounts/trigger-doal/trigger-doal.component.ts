import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { GovukValidators } from 'govuk-components';

import { DoalRequestCreateActionPayload, RequestItemsService, RequestsService } from 'pmrv-api';

import { ItemLinkPipe } from '../../shared/pipes/item-link.pipe';

@Component({
  selector: 'app-trigger-doal',
  templateUrl: './trigger-doal.component.html',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TriggerDoalComponent {
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  isErrorSummaryDisplayed = new BehaviorSubject<boolean>(false);

  readonly years = Array.from({ length: 2030 - 2020 + 1 }, (_, i) => 2020 + i).map((year) => ({
    text: `${year}`,
    value: year,
  }));

  form = this.fb.group({
    year: [null, { validators: GovukValidators.required('Select an option') }],
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

  onSubmit(): void {
    if (!this.form.valid) {
      this.isErrorSummaryDisplayed.next(true);
    } else {
      this.accountId$
        .pipe(
          first(),
          switchMap((accountId) =>
            this.requestsService.processRequestCreateAction(
              {
                requestCreateActionType: 'DOAL',
                requestCreateActionPayload: {
                  payloadType: 'DOAL_REQUEST_CREATE_ACTION_PAYLOAD',
                  year: this.form.get('year').value,
                } as DoalRequestCreateActionPayload,
              },
              String(accountId),
            ),
          ),
          switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
          first(),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(({ items }) => {
          const link = items?.length == 1 ? this.itemLinkPipe.transform(items[0], false) : ['/dashboard'];
          this.router.navigate(link).then();
        });
    }
  }
}
