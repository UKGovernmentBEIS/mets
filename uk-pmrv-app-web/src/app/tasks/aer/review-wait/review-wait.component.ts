import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-review-wait',
  templateUrl: './review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewWaitComponent {
  aerTitle$ = this.aerService.requestMetadata$.pipe(
    map((metadata) => (metadata as AerRequestMetadata).year + ' emissions report'),
  );

  constructor(private readonly aerService: AerService) {}
}
