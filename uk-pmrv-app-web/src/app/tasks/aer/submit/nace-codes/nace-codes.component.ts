import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-nace-codes',
  templateUrl: './nace-codes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodesComponent implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  naceCodes$ = this.aerService.getTask('naceCodes');

  onSubmit(): void {
    this.aerService
      .postTaskSave({}, {}, true, 'naceCodes')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
