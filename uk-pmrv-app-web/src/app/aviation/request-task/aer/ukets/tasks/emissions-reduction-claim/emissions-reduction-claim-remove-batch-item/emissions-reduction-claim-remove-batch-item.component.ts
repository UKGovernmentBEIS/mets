import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { AerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-emissions-reduction-claim-remove-batch-item',
  templateUrl: './emissions-reduction-claim-remove-batch-item.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, SharedModule],
})
export class EmissionsReductionClaimRemoveBatchItemComponent implements OnInit, OnDestroy {
  private previousRouteSegments =
    this.router.getCurrentNavigation()?.previousNavigation?.initialUrl?.root.children.primary.segments;
  private lastSegment;

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: AerEmissionsReductionClaimFormProvider,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.lastSegment =
      this.previousRouteSegments?.length > 0
        ? this.previousRouteSegments[this.previousRouteSegments.length - 1].path
        : null;
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onDelete(): void {
    const removeIndexParam = this.route.snapshot.queryParamMap.get('batchIndex');
    const removeIndex = Number(removeIndexParam);
    if (removeIndex >= 0) {
      this.formProvider.safPurchases.removeAt(removeIndex);

      this.store.aerDelegate
        .saveAer({ saf: this.formProvider.getFormValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          const path =
            this.lastSegment !== 'summary' || this.formProvider.safPurchases.length == 0 ? 'list' : 'summary';

          this.router.navigate(['../', path], {
            relativeTo: this.route,
          });
        });
    }
  }
}
