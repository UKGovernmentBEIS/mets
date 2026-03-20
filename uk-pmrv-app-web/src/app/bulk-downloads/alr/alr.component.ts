import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { BehaviorSubject, map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { BulkDownloadsService } from '../core/bulk-downloads.service';

@Component({
  selector: 'app-alr-bulk-downloads',
  imports: [SharedModule, RouterModule],
  templateUrl: './alr.component.html',
  styles: `
    :host ::ng-deep .govuk-table__caption {
      font-weight: normal;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrBulkDownloadsComponent implements OnInit {
  readonly pageSize = 10;

  tableColumns: GovukTableColumn[] = [{ field: 'period', header: '' }];
  periods$: Observable<any>;

  currentPage$ = new BehaviorSubject<number>(1);

  ngOnInit(): void {
    this.periods$ = this.bulkDownloadsService.getBulkDownloadPeriods('ALR').pipe(
      map((periods) =>
        periods.map((period) => {
          return {
            period,
            downloadUrl: this.bulkDownloadsService.getStreamingBulkDownloadUrl('ALR', period)?.downloadUrl,
          };
        }),
      ),
    );
  }

  constructor(
    readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly bulkDownloadsService: BulkDownloadsService,
  ) {}
}
