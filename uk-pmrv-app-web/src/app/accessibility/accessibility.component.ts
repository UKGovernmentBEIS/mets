import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-accessibility',
  standalone: false,
  templateUrl: './accessibility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessibilityComponent {}
