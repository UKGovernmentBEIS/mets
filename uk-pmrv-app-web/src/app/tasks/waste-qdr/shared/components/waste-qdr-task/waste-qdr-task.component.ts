import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { WasteQdrReturnLinkComponent } from '..';

@Component({
  selector: 'app-waste-qdr-task',
  imports: [SharedModule, WasteQdrReturnLinkComponent],
  template: `
    <app-page-heading *ngIf="heading" [caption]="caption">{{ heading }}</app-page-heading>

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>

    <app-waste-qdr-return-link [returnLink]="returnLink" [title]="returnLinkTitle"></app-waste-qdr-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrTaskComponent {
  @Input() notification: any;
  @Input() returnLink: string;
  @Input() returnLinkTitle: string = 'Complete quarterly data report';
  @Input() heading: string;
  @Input() caption: string;
}
