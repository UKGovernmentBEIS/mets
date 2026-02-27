import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-contact-us',
  standalone: false,
  templateUrl: './contact-us.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactUsComponent {}
