import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AircraftTypeFormProvider } from './aircraft-type-form.provider';

@Component({
  selector: 'app-aircraft-type',
  templateUrl: './aircraft-type.component.html',
  standalone: true,
  imports: [RouterModule],
  providers: [AircraftTypeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypeComponent {}
