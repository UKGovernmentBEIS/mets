import { ChangeDetectionStrategy, Component, Input, OnInit, signal, ViewEncapsulation } from '@angular/core';

import { UserState } from '@core/store';

import { CompanyProfileDTO } from 'pmrv-api';

import { legalEntityTypeMap } from '../../shared/interfaces/legal-entity';

@Component({
  selector: 'app-review-summary',
  templateUrl: './review-summary.component.html',
  styles: `
    del.diffmod {
      display: none;
    }

    ins.diffmod {
      background-color: #fff7bf !important;
    }
  `,
  // eslint-disable-next-line @angular-eslint/use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewSummaryComponent implements OnInit {
  @Input() item: any;
  @Input() taskId: number;
  @Input() roleType: UserState['roleType'];
  @Input() companiesHouse: CompanyProfileDTO;
  @Input() companiesHouseErrorMessage: string;
  showCompaniesHouse = signal(false);

  legalEntityTypeMap = legalEntityTypeMap;

  ngOnInit(): void {
    if (this.roleType === 'REGULATOR' && this.companiesHouse && this.item.legalEntity.referenceNumber) {
      this.showCompaniesHouse.set(true);
    } else {
      this.showCompaniesHouse.set(false);
    }
  }
}
