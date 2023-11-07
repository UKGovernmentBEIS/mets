import { Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukSelectOption } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-location-state-form',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './location-state-form.component.html',
  viewProviders: [existingControlContainer],
})
export class LocationStateFormComponent {
  countriesOptions$: Observable<GovukSelectOption<string>[]> = this.countryService
    .getUkCountries()
    .pipe(
      map((countries) =>
        countries
          .map((country) => ({ text: country.name, value: country.code } as GovukSelectOption<string>))
          .sort((a, b) => a.text.localeCompare(b.text)),
      ),
    );

  constructor(private readonly countryService: CountryService) {}
}
