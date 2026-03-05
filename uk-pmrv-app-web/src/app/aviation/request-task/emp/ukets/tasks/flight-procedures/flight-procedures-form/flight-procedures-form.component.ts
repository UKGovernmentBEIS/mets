import { Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-flight-procedures-form',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './flight-procedures-form.component.html',
  viewProviders: [existingControlContainer],
})
export class FlightProceduresFormComponent {}
