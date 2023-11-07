import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable, takeUntil, tap } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { BatchReissuesResponseDTO, PermitBatchReissueRequestMetadata } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';

interface BatchReissue {
  requestId: string;
  submitter: string;
  submissionDate: string;
  numberOfAccounts: number;
}

@Component({
  selector: 'app-batch-reissue-requests',
  templateUrl: './batch-reissue-requests.component.html',
  styles: [
    `
      button.start-batch-variation {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BatchReissueRequestsComponent implements OnInit {
  @Input() batchReissuesResponse$: Observable<BatchReissuesResponseDTO>;
  @Input() pageSize: number;
  @Output() readonly currentPageChanged = new EventEmitter<number>();

  canInitBatchReissue$: Observable<boolean>;
  batchReissues$: Observable<BatchReissue[]>;
  totalPages$: Observable<number>;

  currentPage$ = new BehaviorSubject<number>(1);

  columns: GovukTableColumn[] = [
    { field: 'requestId', header: 'ID' },
    { field: 'submitter', header: 'Created by' },
    { field: 'submissionDate', header: 'Date created' },
    { field: 'numberOfAccounts', header: 'Emitters' },
  ];

  ngOnInit(): void {
    this.currentPage$
      .pipe(
        tap((page) => this.currentPageChanged.emit(page)),
        takeUntil(this.destroy$),
      )
      .subscribe();

    this.canInitBatchReissue$ = this.batchReissuesResponse$.pipe(map((response) => response.canInitiateBatchReissue));
    this.batchReissues$ = this.batchReissuesResponse$.pipe(
      map((response) =>
        response.requestDetails.map((requestDetails) => {
          const metadata = requestDetails.requestMetadata as PermitBatchReissueRequestMetadata;
          return {
            requestId: requestDetails.id,
            submitter: metadata.submitter,
            submissionDate: requestDetails.requestStatus === 'COMPLETED' ? metadata.submissionDate : null,
            numberOfAccounts:
              requestDetails.requestStatus === 'COMPLETED' ? Object.keys(metadata.accountsReports ?? {})?.length : null,
          };
        }),
      ),
    );
    this.totalPages$ = this.batchReissuesResponse$.pipe(map((response) => response.total));
  }

  viewDetails(id: string) {
    this.router.navigate([id], { relativeTo: this.route });
  }

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}
}
