import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { OperatorImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-recommendation-response-item-form',
  templateUrl: './recommendation-response-item-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationResponseItemFormComponent {
  @Input() heading: string;
  @Input() verificationDataItem: VerificationDataItem;
  @Input() operatorImprovementResponse: OperatorImprovementResponse;
  @Input() attachedFiles: AttachedFile[];
  @Input() formGroup: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() isAviation = false;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.formGroup);
  }
}
