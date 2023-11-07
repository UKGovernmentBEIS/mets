import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {}
