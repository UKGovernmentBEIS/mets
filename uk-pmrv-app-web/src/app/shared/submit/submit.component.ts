import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { RequestInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-submit',
  templateUrl: './submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitComponent {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  @Input() customSubmitContentTemplate: TemplateRef<any>;
  @Input() customSubmittedContentTemplate: TemplateRef<any>;
  @Input() allowSubmit: boolean;
  @Input() isEditable: boolean;
  @Input() isActionSubmitted: boolean;
  @Input() returnUrlConfig: { url: string; text: string };
  @Input() competentAuthority: RequestInfoDTO['competentAuthority'];

  @Output() readonly submitClicked = new EventEmitter<void>();

  constructor(readonly route: ActivatedRoute) {}

  onSubmit(): void {
    this.submitClicked.emit();
  }
}
