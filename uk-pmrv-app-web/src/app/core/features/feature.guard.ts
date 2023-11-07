import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';

import { FeatureName } from './feature.state';
import { FeaturesConfigService } from './features-config.service';

export function isFeatureEnabled(feature: FeatureName): CanMatchFn {
  return () => inject(FeaturesConfigService).isFeatureEnabled(feature);
}
