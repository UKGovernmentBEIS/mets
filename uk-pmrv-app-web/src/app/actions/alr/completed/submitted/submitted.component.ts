import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-alr-action-submitted',
  standalone: true,
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActionCompletedSubmittedComponent {
  actionType = this.alrActionService.requestActionType;

  constructor(private readonly alrActionService: AlrActionService) {}
}
