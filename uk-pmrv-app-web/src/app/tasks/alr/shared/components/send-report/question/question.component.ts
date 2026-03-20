import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';

import { AlrTaskComponent } from '../..';
import { alrQuestionFormProvider } from './question-form.provider';

@Component({
  selector: 'app-alr-send-report-question',
  imports: [AlrTaskComponent, SharedModule],
  templateUrl: './question.component.html',
  providers: [alrQuestionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrSendReportQuestionComponent {
  isEditable = this.alrService.isEditable;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly alrService: AlrService,
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
