export const FEATURES = [
  'aviation',
  'terms',
  'settings',
  'serviceGatewayEnabled',
  'inspectionsWfAccountsTabEnabled',
  'corsia3yearOffsettingEnabled',
  'wastePermitEnabled',
  'wasteQdrEnabled',
  'bdrs2Enabled',
  'nerEnabled',
  'reportingImprovementsEnabled',
] as const;
export type FeatureName = (typeof FEATURES)[number];
export type FeaturesConfig = { [key in FeatureName]?: boolean };

export interface ConfigState {
  features?: FeaturesConfig;
  analytics?: {
    measurementId: string;
    propertyId: string;
  };
  keycloakServerUrl?: string;
}

export const initialState: ConfigState = {
  features: {},
  analytics: {
    measurementId: '',
    propertyId: '',
  },
};
