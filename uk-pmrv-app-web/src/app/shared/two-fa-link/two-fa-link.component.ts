import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-two-fa-link',
  template: `
    <div class="govuk-button-group">
      <a
        govukLink
        [routerLink]="link"
        [state]="{ userId: userId, accountId: accountId, userName: userName, role: role }">
        {{ title }}
      </a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TwoFaLinkComponent {
  @Input() title: string;
  @Input() link: string;
  @Input() userId: string;
  @Input() accountId: number;
  @Input() userName: string;
  @Input() role: string;
}
