import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { EMP_SUBMIT_TASK_TYPES, VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES } from '../submission.util';

@Component({
  selector: 'app-submission-success',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './submission-success.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmissionSuccessComponent extends BaseSuccessComponent {
  private readonly store = inject(RequestTaskStore);
  taskType = this.store.pipe(requestTaskQuery.selectRequestTaskType);
  paymentState = this.store.isPaymentRequired;
  EMP_SUBMIT_TASK_TYPES = EMP_SUBMIT_TASK_TYPES;
  VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES = VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES;
}
