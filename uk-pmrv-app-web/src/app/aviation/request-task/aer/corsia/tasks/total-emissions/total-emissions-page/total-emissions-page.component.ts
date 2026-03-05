import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TotalEmissionsSchemeCorsiaComponent } from '@aviation/request-task/aer/corsia/tasks/total-emissions/shared/total-emissions-scheme-corsia';
import { RequestTaskModule } from '@aviation/request-task/request-task.module';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-total-emissions-page',
  templateUrl: './total-emissions-page.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, RequestTaskModule, TotalEmissionsSchemeCorsiaComponent],
})
export class TotalEmissionsPageComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);

  onSubmit() {
    this.store.aerDelegate
      .saveAer({ totalEmissionsCorsia: undefined }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['./summary'], { relativeTo: this.route });
      });
  }
}
