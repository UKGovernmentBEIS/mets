import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { MiReportsUserDefinedService } from 'pmrv-api';

@Component({
  selector: 'app-delete-custom-report',
  standalone: false,
  templateUrl: './delete-custom-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteCustomReportComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  readonly pendingRequest = inject(PendingRequestService);

  onDelete(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const reportId = Number(idParam);
    if (!idParam || Number.isNaN(reportId)) {
      this.router.navigate(['../../..'], { relativeTo: this.route });
      return;
    }

    this.miReportsService
      .deleteCustomReport(reportId)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() =>
        this.router.navigate(['../../..'], {
          relativeTo: this.route,
          state: { notification: 'The report has been deleted' },
        }),
      );
  }
}
