import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-transferred-co2',
  standalone: false,
  templateUrl: './transferred-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCO2Component {}
