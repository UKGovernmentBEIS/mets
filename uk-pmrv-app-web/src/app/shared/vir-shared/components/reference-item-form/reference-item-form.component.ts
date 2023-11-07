import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

@Component({
  selector: 'app-reference-item-form',
  templateUrl: './reference-item-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReferenceItemFormComponent {
  @Input() heading: string;
  @Input() verificationDataItem: VerificationDataItem;
  @Input() formGroup: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.formGroup);
  }
}
