import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  requestTaskType: RequestTaskDTO['type'];
  isSubmitted: boolean;
  heading: string;
  submitButtonText: string;
  confirmationTitle: string;
}

@Component({
  selector: 'app-ner-complete-withdraw',
  imports: [NerTaskComponent, SharedModule],
  template: `
    @let vm = this.vm();

    @if (vm.isSubmitted) {
      <app-confirmation-shared [title]="vm.confirmationTitle"></app-confirmation-shared>
    } @else {
      <app-ner-task [taskType]="vm.requestTaskType" [heading]="vm.heading" contentWidth="three-quarters">
        <div class="govuk-button-group">
          <button appPendingButton govukButton type="button" (click)="onSubmit()">{{ vm.submitButtonText }}</button>
        </div>
      </app-ner-task>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerCompleteWithdrawComponent {
  private readonly nerService = inject(NerService);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload;
  private readonly requestTaskType = this.nerService.requestTaskType;

  isSubmitted = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const { regulatorReviewOutcome: { opinion } = {} } = this.payload();
    const requestTaskType = this.requestTaskType();
    const isCompleteTask = opinion === 'PROCEED_TO_AUTHORITY';

    return {
      requestTaskType,
      isSubmitted: this.isSubmitted(),
      heading: isCompleteTask
        ? 'Are you sure you want to complete new entrant reserve application review?'
        : 'Are you sure you want to withdraw the new entrant reserve application?',
      submitButtonText: isCompleteTask ? 'Confirm and complete' : 'Yes, withdraw the application',
      confirmationTitle: isCompleteTask
        ? 'New entrant reserve application review completed'
        : 'New entrant reserve application withdrawn',
    };
  });

  onSubmit() {
    this.nerService
      .postRegulatorTaskSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
