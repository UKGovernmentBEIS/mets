import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';

import { GovukValidators } from 'govuk-components';

import { RequestItemsService, RequestsService } from 'pmrv-api';

@Component({
  selector: 'app-audit-year',
  templateUrl: './audit-year.component.html',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditYearComponent implements OnInit {
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  form: UntypedFormGroup = this.fb.group({
    year: [null, [GovukValidators.required('Enter a year')]],
  });
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  years = [];

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly requestsService: RequestsService,
    private readonly route: ActivatedRoute,
    private readonly requestItemsService: RequestItemsService,
    private fb: UntypedFormBuilder,
    private readonly itemLinkPipe: ItemLinkPipe,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.getYears();
  }

  getYears() {
    const currentYear = new Date().getFullYear();
    for (let i = 2021; i <= currentYear; i++) {
      this.years.push(i);
    }
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.accountId$
        .pipe(
          first(),
          switchMap((accountId) =>
            this.requestsService.processRequestCreateAction(
              {
                requestCreateActionType: 'INSTALLATION_AUDIT',
                requestCreateActionPayload: {
                  payloadType: 'INSTALLATION_AUDIT_REQUEST_CREATE_ACTION_PAYLOAD',
                  year: this.form.value.year,
                },
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
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }
}
