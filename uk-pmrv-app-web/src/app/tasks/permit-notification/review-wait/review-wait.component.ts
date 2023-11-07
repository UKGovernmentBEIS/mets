import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-review-wait',
  templateUrl: './review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewWaitComponent {
  constructor(private readonly router: Router, private readonly backService: BackLinkService) {
    if (this.router.getCurrentNavigation()?.extras.state) {
      this.backService.show();
    }
  }
}
