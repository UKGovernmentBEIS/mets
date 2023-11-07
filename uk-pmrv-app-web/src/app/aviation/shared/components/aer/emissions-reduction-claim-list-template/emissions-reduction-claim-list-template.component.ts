import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { AviationAerSafPurchase } from 'pmrv-api';

@Component({
  selector: 'app-emissions-reduction-claim-list-template',
  standalone: true,
  imports: [SharedModule, RouterModule],
  templateUrl: './emissions-reduction-claim-list-template.component.html',
  styles: [
    `
      .amount {
        text-align: left;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimListTemplateComponent implements OnInit {
  @Input() data: {
    purchase: AviationAerSafPurchase;
    files: { downloadUrl: string; fileName: string }[];
  }[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};

  purchases: AviationAerSafPurchase[];

  columns: GovukTableColumn[] = [
    {
      header: 'Fuel name',
      field: 'fuelName',
    },
    {
      header: 'Batch number',
      field: 'batchNumber',
    },
    {
      header: 'Mass of SAF',
      field: 'safMass',
    },
    {
      header: 'Supporting evidence',
      field: 'evidenceFiles',
    },
    {
      header: '',
      field: 'actions',
    },
  ];

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.purchases = this.data?.map((items) => items.purchase) ?? [];
  }

  setQueryParams(index: number) {
    return {
      ...this.queryParams,
      batchIndex: index,
    };
  }

  addAnotherBatch() {
    this.router.navigate(['../', 'add-batch-item'], { relativeTo: this.route, queryParams: { change: true } });
  }

  onContinue() {
    this.router.navigate(['../', 'declaration'], { relativeTo: this.route });
  }
}
