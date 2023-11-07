import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { OperatorImprovementResponse } from 'pmrv-api';

interface ViewModel {
  form: UntypedFormGroup;
  heading: string;
  verificationDataItem: VerificationDataItem;
  isEditable: boolean;
}

@Component({
  selector: 'app-reference-item',
  standalone: true,
  imports: [AsyncPipe, VirSharedModule, ReturnToLinkComponent, NgIf],
  template: `
    <app-reference-item-form
      *ngIf="vm$ | async as vm"
      [formGroup]="vm.form"
      [heading]="vm.heading"
      [verificationDataItem]="vm.verificationDataItem"
      [isEditable]="vm.isEditable"
      (formSubmit)="onSubmit()"
    ></app-reference-item-form>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReferenceItemComponent {
  form = new FormGroup(
    {
      isAddressed: this.formProvider.isAddressedCtrl,
      addressedDescription: this.formProvider.addressedDescriptionCtrl,
      addressedDate: this.formProvider.addressedDateCtrl,
      addressedDescription2: this.formProvider.addressedDescription2Ctrl,
    },
    { updateOn: 'change' },
  );
  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        heading: `Respond to ${this.verificationDataItem.reference}`,
        verificationDataItem: this.verificationDataItem,
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
        this.router.navigate(['upload-evidence-question'], {
          relativeTo: this.route,
        });
      });
  }

  private getFormData(): OperatorImprovementResponse {
    return {
      ...this.formProvider.getFormValue(),
      isAddressed: this.form.get('isAddressed').value,
      addressedDescription: this.form.get('isAddressed').value
        ? this.form.get('addressedDescription').value
        : this.form.get('addressedDescription2').value,
      addressedDate: this.form.get('isAddressed').value ? this.form.get('addressedDate').value : null,
    };
  }
}
