import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../../store';
import { emissionSourcesQuery } from '../../emission-sources/store/emission-sources.selectors';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures-risk-assessment',
  templateUrl: './management-procedures-risk-assessment.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresRiskAssessmentComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private destroy$ = inject(DestroySubject);

  form = new FormGroup({
    riskAssessmentFile: this.formProvider.riskAssessmentFileCtrl,
  });

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    const file = this.form.value.riskAssessmentFile;
    this.store.empUkEtsDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setManagementProcedures(this.formProvider.getFormValue());
        this.store.empUkEtsDelegate.addEmpAttachment({ [file.uuid]: file.file.name });
        this.store.pipe(emissionSourcesQuery.selectEmissionSources, takeUntil(this.destroy$)).subscribe((es) => {
          if (
            es?.aircraftTypes?.length &&
            es.aircraftTypes.every(
              (aircraftType) => aircraftType.fuelConsumptionMeasuringMethod === 'BLOCK_ON_BLOCK_OFF',
            )
          ) {
            this.formProvider.removeUpliftQuantityCrossChecksForm();
            this.router.navigate(['../environmental-management'], {
              relativeTo: this.route,
            });
          } else {
            this.formProvider.addUpliftQuantityCrossChecksForm();
            this.router.navigate(['../uplift-quantity'], { relativeTo: this.route });
          }
        });
      });
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', 'attachment', uuid];
  }
}
