import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { getWorkflowTypeText, TASKS_RETURN_TO_OPERATOR_FORM } from '@tasks/shared/core';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  heading: string;
  caption: string;
  taskType: RequestTaskDTO['type'];
  returnLinkLevelsUp: number;
  isEditable: boolean;
  textAreaHint: string;
}

@Component({
  selector: 'app-task-return-to-operator',
  imports: [TaskSharedModule, SharedModule],
  template: `
    @let vm = this.vm();

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-page-heading [caption]="vm.caption">{{ vm.heading }}</app-page-heading>
        <app-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" [hideSubmit]="!vm.isEditable">
          <div govuk-textarea formControlName="changesRequired" [maxLength]="10000" [hint]="vm.textAreaHint"></div>
        </app-wizard-step>
      </div>
    </div>
    <app-task-return-link [taskType]="vm.taskType" [levelsUp]="vm.returnLinkLevelsUp"></app-task-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TasksReturnToOperatorComponent {
  private readonly store = inject(CommonTasksStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly isEditable = toSignal(this.store.pipe(map((state) => state.isEditable)));
  private readonly requestTaskType = toSignal(this.store.pipe(map((state) => state.requestTaskItem.requestTask.type)));

  form = inject(TASKS_RETURN_TO_OPERATOR_FORM);

  vm: Signal<ViewModel> = computed(() => {
    const taskType = this.requestTaskType();
    const workflowTypeText = getWorkflowTypeText(taskType);

    return {
      heading: 'Changes required by the operator',
      caption: 'Return to operator for changes',
      taskType,
      returnLinkLevelsUp: 1,
      isEditable: this.isEditable(),
      textAreaHint: `
          The operator will be notified by email that the ${workflowTypeText} has been returned for changes.
          <br />
          Verification progress is saved so that you can continue your review if the operator resubmits the ${workflowTypeText} for
          verification.
          <br />
          This will be sent to the operator when you return the ${workflowTypeText}
      `,
    };
  });

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
