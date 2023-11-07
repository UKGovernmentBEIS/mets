import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly store: CommonTasksStore,
    private readonly backService: BackLinkService,
  ) {
    if (this.router.getCurrentNavigation()?.extras.state) {
      this.backService.show();
    }
  }

  onCancelTask() {
    this.router.navigate(['cancel'], { relativeTo: this.route.parent.parent.parent });
  }
}
