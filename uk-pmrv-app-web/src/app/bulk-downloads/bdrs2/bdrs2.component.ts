import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { BehaviorSubject, map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { BulkDownloadsService } from '../core/bulk-downloads.service';

@Component({
  selector: 'app-bdrs2-bulk-downloads',
  imports: [SharedModule, RouterModule],
  templateUrl: './bdrs2.component.html',
  styles: `
    :host ::ng-deep .govuk-table__caption {
      font-weight: normal;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2BulkDownloadsComponent implements OnInit {
  tableColumns: GovukTableColumn[] = [{ field: 'period', header: '' }];
  periods$: Observable<any>;

  currentPage$ = new BehaviorSubject<number>(1);

  ngOnInit(): void {
    this.periods$ = this.bulkDownloadsService.getBulkDownloadPeriods('BDRS2').pipe(
      map((periods) =>
        periods.map((period) => {
          return {
            period: period.split('-').join(' - '),
            downloadUrl: this.bulkDownloadsService.getStreamingBulkDownloadUrl('BDRS2', period)?.downloadUrl,
          };
        }),
      ),
    );
  }

  constructor(
    readonly router: Router,
    private readonly bulkDownloadsService: BulkDownloadsService,
  ) {}
}
