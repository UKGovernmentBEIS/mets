import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-doal-closed',
  standalone: false,
  templateUrl: './closed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClosedComponent {}
