import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { TransferCO2 } from 'pmrv-api';

import { createTransferredDetailsFormGroup } from './transfer-details-form.component';

@Component({
  selector: 'app-transfer-details-template',
  templateUrl: './transfer-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferDetailsTemplateComponent implements OnInit {
  @Input() transfer: Omit<TransferCO2, 'transferType'>;
  @Input() isEditable: boolean;
  @Input() heading: string;
  @Input() showBackLink: boolean;
  @Input() returnToLink: string;
  @Input() submitText = 'Save and continue';

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  form: UntypedFormGroup;

  ngOnInit(): void {
    this.form = createTransferredDetailsFormGroup(this.transfer, this.isEditable);
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
