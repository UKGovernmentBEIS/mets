import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { OperatorImprovementResponse } from 'pmrv-api';

interface ViewModel {
  form: UntypedFormGroup;
  heading: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-upload-evidence-question',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule],
  template: `
    <app-wizard-step
      *ngIf="vm$ | async as vm"
      (formSubmit)="onSubmit()"
      [formGroup]="vm.form"
      submitText="Continue"
      [heading]="vm.heading"
      [hideSubmit]="vm.isEditable === false">
      <div formControlName="uploadEvidence" govuk-radio>
        <govuk-radio-option [value]="true" label="Yes"></govuk-radio-option>
        <govuk-radio-option [value]="false" label="No"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadEvidenceQuestionComponent {
  form = new FormGroup(
    {
      uploadEvidence: this.formProvider.uploadEvidenceCtrl,
    },
    { updateOn: 'change' },
  );

  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        heading: 'Would you like to upload evidence to support your response?',
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ReferenceItemFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveVir(this.getFormData(), this.verificationDataItem.reference, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.formProvider.setFormValue(this.getFormData());

        const nextRoute = this.form.get('uploadEvidence').value ? '../upload-evidence-files' : '../summary';
        this.router.navigate([nextRoute], {
          relativeTo: this.route,
        });
      });
  }

  private getFormData(): OperatorImprovementResponse {
    return {
      ...this.formProvider.getFormValue(),
      uploadEvidence: this.form.get('uploadEvidence').value,
      ...(!this.form.get('uploadEvidence').value
        ? {
            files: [],
          }
        : {}),
    };
  }
}
