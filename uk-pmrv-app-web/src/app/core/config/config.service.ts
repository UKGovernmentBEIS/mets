import { Injectable } from '@angular/core';

import { map, Observable, tap } from 'rxjs';

import { selectIsFeatureEnabled, selectMeasurementId, selectPropertyId } from '@core/config/config.selectors';
import { ConfigState, FeatureName } from '@core/config/config.state';
import { ConfigStore } from '@core/config/config.store';

import { UIConfigurationService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  constructor(
    private readonly store: ConfigStore,
    private readonly configurationService: UIConfigurationService,
  ) {}

  initConfigState(): Observable<ConfigState> {
    return this.configurationService.getUIConfiguration().pipe(
      tap((props) => this.store.setState({ ...props } as ConfigState)),
      map(() => this.store.getState()),
    );
  }

  isFeatureEnabled(feature: FeatureName): Observable<boolean> {
    return this.store.pipe(selectIsFeatureEnabled(feature));
  }

  getMeasurementId(): Observable<string> {
    return this.store.pipe(selectMeasurementId);
  }
  getPropertyId(): Observable<string> {
    return this.store.pipe(selectPropertyId);
  }
}
