import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  changesRequired: string;
}

@Component({
  selector: 'app-verification-returned-to-operator',
  imports: [ActionSharedModule, SharedModule],
  template: `
    @let vm = this.vm();

    <app-base-action-container-component
      [header]="vm.header"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="vm.expectedActionType"></app-base-action-container-component>

    <ng-template #customContentTemplate>
      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Changes required from operator</dt>
          <dd govukSummaryListRowValue class="pre-wrap">
            {{ vm.changesRequired }}
          </dd>
        </div>
      </dl>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationReturnedToOperatorComponent {
  private readonly route = inject(ActivatedRoute);

  vm: Signal<ViewModel> = computed(() => {
    const { changesRequired, actionType } = this.route.snapshot.data.input || {};
    const header = this.getHeaderText(actionType);

    return { header, expectedActionType: [actionType], changesRequired };
  });

  getHeaderText(requestActionType: RequestActionDTO['type']): string {
    const itemActionTypePipe = new ItemActionTypePipe();

    return itemActionTypePipe.transform(requestActionType);
  }
}
