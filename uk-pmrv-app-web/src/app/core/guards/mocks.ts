import { AuthService } from '@core/services/auth.service';
import { KeycloakService } from 'keycloak-angular';
import Mock = jest.Mock;
import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'pmrv-api';

export const mockKeycloakService: Record<keyof KeycloakService, Mock> = {
  login: jest.fn(),
  logout: jest.fn(),
  isLoggedIn: jest.fn(),
  loadUserProfile: jest.fn(),
} as any;

export const mockAuthService: Record<keyof AuthService, Mock> = {
  checkUser: jest.fn(),
  login: jest.fn(),
  logout: jest.fn(),
  loadUser: jest.fn(),
  loadUserState: jest.fn(),
  loadUserProfile: jest.fn(),
  loadTerms: jest.fn(),
  loadIsLoggedIn: jest.fn(),
};

export const mockUsersService: Record<keyof UsersService, Mock> = {
  getCurrentUser: jest.fn(),
  registerUserLastLoginDomain: jest.fn(),
} as any;

export const mockAuthorityService: Record<keyof AuthoritiesService, Mock> = {
  getCurrentUserState: jest.fn(),
} as any;

export const mockTermsAndConditionsService: Record<keyof TermsAndConditionsService, Mock> = {
  getLatestTerms: jest.fn(),
} as any;
