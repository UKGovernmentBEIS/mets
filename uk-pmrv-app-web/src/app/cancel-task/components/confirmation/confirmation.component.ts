import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { isApplicationTextTypes } from '@cancel-task/cancel-action.util';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-cancel-confirmation',
  standalone: false,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="{{ isApplicationText() }} cancelled"></govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard">Return to dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent extends BaseSuccessComponent {
  private readonly commonTasksStore = inject(CommonTasksStore);
  private readonly requestTaskType = toSignal(this.commonTasksStore.requestTaskType$);

  isApplicationText = computed(() => {
    const requestTaskType = this.requestTaskType();

    return isApplicationTextTypes.includes(requestTaskType) ? 'Application' : 'Task';
  });
}
