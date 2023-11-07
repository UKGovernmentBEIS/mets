import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { OperatorImprovementResponse, RegulatorImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-respond-item-form',
  templateUrl: './respond-item-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RespondItemFormComponent {
  @Input() heading: string;
  @Input() verificationDataItem: VerificationDataItem;
  @Input() operatorImprovementResponse: OperatorImprovementResponse;
  @Input() documentFiles: AttachedFile[];
  @Input() regulatorImprovementResponse: RegulatorImprovementResponse;
  @Input() formGroup: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.formGroup);
  }
}
