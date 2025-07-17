import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';

import { BehaviorSubject, takeUntil, tap } from 'rxjs';

import { OperatorDetailsCorsiaFormProvider } from '@aviation/request-task/emp/corsia/tasks/operator-details';
import { OperatorDetailsQuery } from '@aviation/request-task/emp/corsia/tasks/operator-details/store/operator-details.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsActivitiesDescriptionPipe } from '@aviation/shared/pipes/operator-details-activities-description.pipe';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { UuidFilePair } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { AviationCorsiaOperatorDetails, EmpCorsiaOperatorDetails, EmpOperatorDetails } from 'pmrv-api';

import { OperatorDetailsSubsidiaryListComponent } from '../operator-details-subsidiary-list/operator-details-subsidiary-list.component';
import { transformFiles } from '../utils/operator-details-summary.util';

@Component({
  selector: 'app-operator-details-summary-template',
  templateUrl: './operator-details-summary-template.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    SharedModule,
    OperatorDetailsFlightIdentificationTypePipe,
    OperatorDetailsLegalStatusTypePipe,
    OperatorDetailsActivitiesDescriptionPipe,
    OperatorDetailsSubsidiaryListComponent,
  ],
})
export class OperatorDetailsSummaryTemplateComponent implements OnInit {
  @Input() isEditable = false;
  @Input() data: EmpOperatorDetails | EmpCorsiaOperatorDetails | AviationCorsiaOperatorDetails;

  @Input() certificationFiles: { fileName: string; downloadUrl: string }[];
  @Input() evidenceFiles: { fileName: string; downloadUrl: string }[];
  @Input() changeUrlQueryParams: Params = {};

  private operatorDetails: EmpCorsiaOperatorDetails;
  private sectionsWithAttachments = {
    airOperatingCertificate: 'certificateFiles',
    organisationStructure: 'evidenceFiles',
  };
  subsidiaryCompaniesNameControl = 'subsidiaryCompanies';
  subsidiaryCompanies$ = new BehaviorSubject<EmpCorsiaOperatorDetails['subsidiaryCompanies']>([]);

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formDetailsProvider: OperatorDetailsCorsiaFormProvider,
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.subsidiaryCompanies$.next((this.data as EmpCorsiaOperatorDetails).subsidiaryCompanies);
  }

  getOperatorDetails() {
    this.store
      .pipe(OperatorDetailsQuery.selectOperatorDetails, takeUntil(this.destroy$))
      .subscribe((operatorDetails: EmpCorsiaOperatorDetails) => {
        this.operatorDetails = operatorDetails;
      });
  }

  private addFilesToEmpAttachments(currentForm: FormGroup, formControlName: string) {
    (currentForm.value[formControlName] || []).forEach((file: UuidFilePair) => {
      this.store.empDelegate.addEmpAttachment({ [file.uuid]: file.file.name });
    });
  }

  handleSubsidiaryList(index: number) {
    this.formDetailsProvider.removeSubsidiaryCompanyItem(index);
    this.getOperatorDetails();

    const form = this.formDetailsProvider.form;
    const currentForm = form.controls[this.subsidiaryCompaniesNameControl];

    const operatorDetails = {
      ...this.operatorDetails,
      ...{
        subsidiaryCompanies: this.operatorDetails.subsidiaryCompanies.filter((_, i) => i !== index),
        subsidiaryCompanyExist: form.value.subsidiaryCompanyExist,
      },
    } as EmpCorsiaOperatorDetails;

    if (
      this.subsidiaryCompaniesNameControl && operatorDetails.subsidiaryCompanies?.length > 0
        ? currentForm.valid
        : form?.valid
    ) {
      (this.store.empDelegate as EmpCorsiaStoreDelegate)
        .saveEmp({ operatorDetails }, 'in progress')
        .pipe(
          this.pendingRequestService.trackRequest(),
          tap(() => {
            const sectionWithAttachment = this.sectionsWithAttachments[this.subsidiaryCompaniesNameControl];
            if (sectionWithAttachment) {
              this.addFilesToEmpAttachments(currentForm as any, sectionWithAttachment);
            }
          }),
        )
        .subscribe(() => {
          const subsidiaryCompanies = form.value.subsidiaryCompanies as EmpCorsiaOperatorDetails['subsidiaryCompanies'];

          this.subsidiaryCompanies$.next(
            subsidiaryCompanies.map((subsidiaryCompany) => ({
              ...subsidiaryCompany,
              airOperatingCertificate: {
                ...subsidiaryCompany.airOperatingCertificate,
                certificateFiles: subsidiaryCompany.airOperatingCertificate.certificateExist
                  ? (transformFiles(
                      subsidiaryCompany.airOperatingCertificate?.certificateFiles,
                      `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`,
                    ) as any)
                  : [],
              },
            })),
          );
        });
    }
  }
}
