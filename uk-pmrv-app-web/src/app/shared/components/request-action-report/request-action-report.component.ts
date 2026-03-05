import { ChangeDetectionStrategy, Component, Input, OnDestroy, Type, ViewChild, ViewContainerRef } from '@angular/core';

import { filter, Subscription } from 'rxjs';

import { PrintComponent } from '@shared/print/print.component';
import { RequestActionReportService } from '@shared/services/request-action-report.service';

@Component({
  selector: 'app-request-action-report',
  templateUrl: './request-action-report.component.html',
  styleUrl: './request-action-report.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestActionReportComponent implements OnDestroy {
  @Input() dataComponent: Type<any>;

  @ViewChild('printContainerRef', { read: ViewContainerRef })
  printContainerRef: ViewContainerRef;

  @ViewChild('printComp') printComponent: PrintComponent;

  private printSubscription: Subscription;

  constructor(private requestActionReportService: RequestActionReportService) {
    this.printSubscription = this.requestActionReportService.printReport$
      .pipe(filter((print) => !!print))
      .subscribe(() => this.printComponent.print());
  }

  ngOnDestroy(): void {
    this.requestActionReportService.clear();
    this.printSubscription.unsubscribe();
  }

  printRequestAction() {
    this.printContainerRef.clear();
    this.printContainerRef.createComponent(this.dataComponent);
  }
}
