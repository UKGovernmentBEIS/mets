import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AircraftTypeFormProvider } from './aircraft-type-form.provider';

@Component({
  selector: 'app-aircraft-type',
  imports: [RouterModule],
  templateUrl: './aircraft-type.component.html',
  providers: [AircraftTypeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypeComponent {}
