import { ChangeDetectionStrategy, Component, Inject, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';

import { takeUntil, tap } from 'rxjs';

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
export class OperatorDetailsSummaryTemplateComponent {
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

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formDetailsProvider: OperatorDetailsCorsiaFormProvider,
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {}

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

    const currentForm = this.formDetailsProvider.form.controls[this.subsidiaryCompaniesNameControl];
    const operatorDetails = {
      ...this.operatorDetails,
      ...{ subsidiaryCompanies: this.formDetailsProvider.form.value.subsidiaryCompanies },
    };

    if (this.subsidiaryCompaniesNameControl ? currentForm.valid : this.formDetailsProvider.form?.valid) {
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
          this.router.navigate(['../summary'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
