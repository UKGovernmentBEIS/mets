import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { unparseCsv } from '@aviation/request-task/util';
import { FlightIdentificationTypePipe } from '@aviation/shared/pipes/flight-identification-type.pipe';
import { OperatorDetailsActivitiesDescriptionPipe } from '@aviation/shared/pipes/operator-details-activities-description.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpCorsiaOperatorDetails } from 'pmrv-api';

import { transformFiles } from '../utils/operator-details-summary.util';

@Component({
  selector: 'app-operator-details-subsidiary-list',
  templateUrl: './operator-details-subsidiary-list.component.html',
  styleUrl: './operator-details-subsidiary-list.component.scss',
  standalone: true,
  imports: [
    GovukComponentsModule,
    RouterLinkWithHref,
    NgIf,
    NgFor,
    SharedModule,
    FlightIdentificationTypePipe,
    OperatorDetailsActivitiesDescriptionPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsSubsidiaryListComponent implements OnInit {
  @Input() subsidiaryCompaniesData$: Observable<EmpCorsiaOperatorDetails['subsidiaryCompanies']>;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
  @Input() linkTo: string;

  @Output() readonly removeSubsidiaryItem = new EventEmitter<number>();

  subsidiaryCompanies$: Observable<EmpCorsiaOperatorDetails['subsidiaryCompanies']>;

  constructor(protected readonly store: RequestTaskStore) {}

  ngOnInit(): void {
    this.subsidiaryCompanies$ = this.subsidiaryCompaniesData$.pipe(
      map((subsidiaryCompanies) => this.getTransformedSubsidiaryFiles(subsidiaryCompanies || [])),
    );
  }

  onRemoveSubsidiaryCompany(index: number) {
    this.removeSubsidiaryItem.emit(index);
  }

  getTransformedSubsidiaryFiles(subsidiaryCompanies: EmpCorsiaOperatorDetails['subsidiaryCompanies']) {
    if (subsidiaryCompanies.length > 0) {
      for (const subsidiaryCompany of subsidiaryCompanies) {
        if (subsidiaryCompany.airOperatingCertificate.certificateExist) {
          if (
            subsidiaryCompany.airOperatingCertificate.certificateFiles &&
            !(subsidiaryCompany.airOperatingCertificate?.certificateFiles[0] as any)?.downloadUrl &&
            typeof subsidiaryCompany.airOperatingCertificate?.certificateFiles[0] === 'object'
          ) {
            subsidiaryCompany.airOperatingCertificate.certificateFiles = transformFiles(
              subsidiaryCompany.airOperatingCertificate.certificateFiles ?? [],
              this.store.empDelegate.baseFileAttachmentDownloadUrl + '/',
            ) as any;
          }
        }

        if (
          subsidiaryCompany.flightIdentification?.aircraftRegistrationMarkings &&
          typeof subsidiaryCompany.flightIdentification?.aircraftRegistrationMarkings === 'object'
        ) {
          subsidiaryCompany.flightIdentification = {
            ...subsidiaryCompany.flightIdentification,
            aircraftRegistrationMarkings: unparseCsv(
              subsidiaryCompany.flightIdentification?.aircraftRegistrationMarkings,
            ),
          } as any;
        }
      }
    }
    return subsidiaryCompanies;
  }
}
