import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { map, Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { BulkDownloadWorkflowDTO } from 'pmrv-api';

import { BulkDownloadsService } from './core/bulk-downloads.service';

@Component({
  selector: 'app-bulk-downloads',
  imports: [SharedModule, RouterModule],
  templateUrl: './bulk-downloads.component.html',
  styles: `
    :host ::ng-deep th.govuk-table__header {
      border-bottom: none;
    }
    :host ::ng-deep .govuk-table__caption {
      font-weight: normal;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BulkDownloadsComponent implements OnInit {
  tableColumns: GovukTableColumn[] = [{ field: 'name', header: '' }];
  enabledWorkflows$: Observable<BulkDownloadWorkflowDTO[]>;

  ngOnInit(): void {
    this.enabledWorkflows$ = this.bulkDownloadsService.getBulkDownloadWorkflows$.pipe(
      map((workflows) =>
        workflows.map((workflow) => ({
          name: workflow,
          link: `../bulk-downloads/${workflow.toLowerCase()}`,
        })),
      ),
    );
  }

  constructor(
    readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly bulkDownloadsService: BulkDownloadsService,
  ) {}
}
