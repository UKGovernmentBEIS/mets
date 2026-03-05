import { ChangeDetectionStrategy, Component, Input, OnInit, signal } from '@angular/core';

import { UserState } from '@core/store';

import { CompanyProfileDTO, InstallationOperatorDetails } from 'pmrv-api';

@Component({
  selector: 'app-installation-details-summary',
  templateUrl: './installation-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDetailsSummaryComponent implements OnInit {
  @Input() cssClass: string;
  @Input() installationOperatorDetails: InstallationOperatorDetails;
  @Input() hideSubheadings = false;
  @Input() hasBorders = false;

  @Input() companiesHouse: CompanyProfileDTO;
  @Input() companiesHouseErrorMessage: string;
  @Input() roleType: UserState['roleType'];
  showCompaniesHouse = signal(false);

  installationLocation: any;

  ngOnInit(): void {
    this.installationLocation = {
      ...this.installationOperatorDetails.installationLocation,
    };

    if (
      this.roleType === 'REGULATOR' &&
      this.companiesHouse &&
      this.installationOperatorDetails.companyReferenceNumber
    ) {
      this.showCompaniesHouse.set(true);
    } else {
      this.showCompaniesHouse.set(false);
    }
  }
}
