import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-email-link-invalid',
  standalone: false,
  templateUrl: './email-link-invalid.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailLinkInvalidComponent {}
