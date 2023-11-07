import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-timeline-rde-submitted',
  template: `
    <app-request-action-heading
      headerText="Response to request for deadline extension"
      [timelineCreationDate]="store.select('requestActionCreationDate') | async"
    >
    </app-request-action-heading>
    <dl govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="true">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Response</dt>
        <dd govukSummaryListRowValue>Rejected</dd>
      </div>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Reason</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ reason$ | async }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TimelineComponent implements OnInit {
  constructor(
    readonly store: RdeStore,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  reason$ = this.store.pipe(map((state) => state.reason));

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }
}
