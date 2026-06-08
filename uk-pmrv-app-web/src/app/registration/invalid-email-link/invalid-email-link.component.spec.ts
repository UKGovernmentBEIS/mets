import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { mockKeycloak } from '@core/guards/mocks';
import { AuthService } from '@core/services/auth.service';
import { ActivatedRouteSnapshotStub } from '@testing';
import Keycloak from 'keycloak-js';

import { GovukComponentsModule } from 'govuk-components';

import { InvalidEmailLinkComponent } from './invalid-email-link.component';

describe('InvalidEmailLinkComponent', () => {
  let component: InvalidEmailLinkComponent;
  let fixture: ComponentFixture<InvalidEmailLinkComponent>;
  let route: ActivatedRoute;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, RouterTestingModule],
      declarations: [InvalidEmailLinkComponent],
      providers: [
        { provide: Keycloak, useValue: mockKeycloak },
        { provide: APP_BASE_HREF, useValue: '/installation-aviation/' },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidEmailLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    route = TestBed.inject(ActivatedRoute);
    authService = TestBed.inject(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain email error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'EMAIL1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain(
      'The email verification link has expired',
    );
  });

  it('should contain user error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'USER1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('A sign in cannot be created');
  });

  it('should contain token error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'TOKEN1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Invalid token');
  });

  it('should contain form error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'FORM1001' });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Form validation failed');
  });

  it('should contain default error', () => {
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: null });

    component.ngOnInit();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('li')[0].textContent).toContain('Invalid token');
  });

  it('should sign in on anchor click', () => {
    const signInSpy = jest.spyOn(authService, 'login');
    route.snapshot = new ActivatedRouteSnapshotStub(null, { code: 'USER1001' });

    component.ngOnInit();
    fixture.detectChanges();

    const anchors = fixture.nativeElement.querySelectorAll('a');

    anchors[1].click();

    expect(signInSpy).toHaveBeenCalled();
  });
});
