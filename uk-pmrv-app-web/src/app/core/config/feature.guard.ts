import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';

import { ConfigService } from '@core/config/config.service';
import { FeatureName } from '@core/config/config.state';

export function isFeatureEnabled(feature: FeatureName): CanMatchFn {
  return () => inject(ConfigService).isFeatureEnabled(feature);
}
