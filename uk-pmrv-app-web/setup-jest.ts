import './jest-global-mocks';
import '@testing-library/jest-dom';
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';

setupZoneTestEnv();
