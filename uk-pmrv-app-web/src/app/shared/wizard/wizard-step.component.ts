import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, filter, startWith, take } from 'rxjs';

@Component({
  selector: 'app-wizard-step',
  templateUrl: './wizard-step.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WizardStepComponent {
  @Input() showBackLink = false;
  @Input() formGroup: UntypedFormGroup;
  @Input() heading: string;
  @Input() caption: string;
  @Input() submitText = 'Continue';
  @Input() hideSubmit: boolean;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  isSummaryDisplayedSubject = new BehaviorSubject(false);

  onSubmit(): void {
    this.formGroup.statusChanges
      .pipe(
        startWith(this.formGroup.status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe((status) => {
        switch (status) {
          case 'VALID':
            this.formSubmit.emit(this.formGroup);
            break;
          case 'INVALID':
            this.formGroup.markAllAsTouched();
            this.isSummaryDisplayedSubject.next(true);
            break;
        }
      });
  }
}
