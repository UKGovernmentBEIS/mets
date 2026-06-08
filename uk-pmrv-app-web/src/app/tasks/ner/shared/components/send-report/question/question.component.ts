import { ChangeDetectionStrategy, Component, computed, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';

import { NerTaskComponent } from '../../ner-task/ner-task.component';
import { nerQuestionFormProvider } from './question-form.provider';

@Component({
  selector: 'app-ner-send-report-question',
  imports: [NerTaskComponent, SharedModule],
  templateUrl: './question.component.html',
  providers: [nerQuestionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerSendReportQuestionComponent {
  isEditable = this.nerService.isEditable;
  readonly requestTaskType = this.nerService.requestTaskType;
  private readonly requestTaskItem = this.nerService.requestTaskItem;

  readonly competentAuthority = computed(() => {
    const requestTaskItem = this.requestTaskItem();
    const componentAuthority = requestTaskItem.requestInfo?.competentAuthority;
    return componentAuthority;
  });

  constructor(
    @Inject(NER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly nerService: NerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    this.router.navigate(['../'], {
      relativeTo: this.route,
      queryParams: { sendTo: this.form.get('needsVerification').value ? 'verifier' : 'regulator' },
    });
  }
}
