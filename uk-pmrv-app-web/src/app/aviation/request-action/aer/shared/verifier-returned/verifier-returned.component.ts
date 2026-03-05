import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLinkWithHref } from '@angular/router';

import { map } from 'rxjs';

import { RequestActionStore } from '@aviation/request-action/store';
import { getRequestActionHeader } from '@aviation/request-action/util';
import { AerVerifierReturnedTemplateComponent } from '@aviation/shared/components/aer-verifier-returned-template/aer-verifier-returned-template.component';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaVerificationReturnedToOperatorRequestActionPayload,
  AviationAerUkEtsVerificationReturnedToOperatorRequestActionPayload,
} from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  changesRequired: string;
}

@Component({
  selector: 'app-aviation-aer-verifier-returned',
  standalone: true,
  imports: [AerVerifierReturnedTemplateComponent, SharedModule, RouterLinkWithHref],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-request-action-heading [headerText]="vm.pageHeader" [timelineCreationDate]="vm.creationDate">
        <app-aer-verifier-returned-template
          [changesRequired]="vm.changesRequired"
          [isEditable]="false"></app-aer-verifier-returned-template>
        <a govukLink routerLink="../../">
          Return to: {{ this.router.url.includes('workflows') ? 'Emissions report' : 'Dashboard' }}
        </a>
      </app-request-action-heading>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAerVerifierReturnedComponent {
  private readonly requestActionItem = toSignal(this.store.pipe(map((state) => state.requestActionItem)));

  vm: Signal<ViewModel> = computed(() => {
    const requestActionItem = this.requestActionItem();
    const payload = requestActionItem.payload as
      | AviationAerUkEtsVerificationReturnedToOperatorRequestActionPayload
      | AviationAerCorsiaVerificationReturnedToOperatorRequestActionPayload;

    return {
      pageHeader: getRequestActionHeader(requestActionItem.type),
      creationDate: requestActionItem.creationDate,
      changesRequired: payload.changesRequired,
    } as ViewModel;
  });

  constructor(
    public store: RequestActionStore,
    readonly router: Router,
  ) {}
}
