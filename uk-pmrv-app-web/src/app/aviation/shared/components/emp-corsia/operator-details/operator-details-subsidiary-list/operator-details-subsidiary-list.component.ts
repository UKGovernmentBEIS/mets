import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { FlightIdentificationTypePipe } from '@aviation/shared/pipes/flight-identification-type.pipe';
import { TypeOfFlightsPipe } from '@aviation/shared/pipes/type-of-flights.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { transformFiles } from '../utils/operator-details-summary.util';

@Component({
  selector: 'app-operator-details-subsidiary-list',
  templateUrl: './operator-details-subsidiary-list.component.html',
  styleUrls: ['./operator-details-subsidiary-list.component.scss'],
  standalone: true,
  imports: [
    GovukComponentsModule,
    RouterLinkWithHref,
    NgIf,
    NgFor,
    SharedModule,
    FlightIdentificationTypePipe,
    TypeOfFlightsPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsSubsidiaryListComponent implements OnInit {
  @Input() subsidiaryCompanyData;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
  @Input() linkTo: string;
  listCompanyData = null;

  @Output() readonly removeSubsidiaryItem = new EventEmitter<number>();

  constructor(protected readonly store: RequestTaskStore) {}

  ngOnInit(): void {
    this.listCompanyData = { ...this.getTransformedSubsidiaryFiles(this.subsidiaryCompanyData) };
  }

  onRemoveDataGap(index: number) {
    this.removeSubsidiaryItem.emit(index);
  }

  getTransformedSubsidiaryFiles(operatorDetails) {
    if (operatorDetails?.subsidiaryCompanies) {
      for (const item of operatorDetails.subsidiaryCompanies) {
        if (item.airOperatingCertificate.certificateExist) {
          if (
            item.airOperatingCertificate.certificateFiles &&
            !item.airOperatingCertificate?.certificateFiles[0]?.downloadUrl &&
            typeof item.airOperatingCertificate?.certificateFiles[0] === 'object'
          ) {
            item.airOperatingCertificate.certificateFiles = transformFiles(
              item.airOperatingCertificate.certificateFiles ?? [],
              this.store.empDelegate.baseFileAttachmentDownloadUrl + '/',
            );
          }
        }
      }
    }
    return operatorDetails;
  }
}
