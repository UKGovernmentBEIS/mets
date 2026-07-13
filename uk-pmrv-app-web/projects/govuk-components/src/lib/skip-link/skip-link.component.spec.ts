import { Component, NgZone } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router, RouterOutlet } from '@angular/router';

import { SkipLinkComponent } from './skip-link.component';

describe('SkipLinkComponent', () => {
  let component: SkipLinkComponent;
  let fixture: ComponentFixture<HostComponent>;
  let router: Router;

  @Component({ template: '' })
  class RoutedComponent {}

  @Component({
    imports: [SkipLinkComponent, HostComponent, RoutedComponent, RouterOutlet],
    template: `
      <govuk-skip-link />
      <router-outlet />
    `,
  })
  class HostComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SkipLinkComponent, HostComponent, RoutedComponent],
      providers: [provideRouter([{ path: 'test', component: RoutedComponent }])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HostComponent);
    component = fixture.debugElement.query(By.directive(SkipLinkComponent)).componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly set routerLink', async () => {
    const originalNavigateByUrl = router.navigateByUrl;
    jest
      .spyOn(router, 'navigateByUrl')
      .mockImplementation((...options) =>
        TestBed.inject(NgZone).run(() => originalNavigateByUrl.apply(router, options)),
      );
    await router.navigateByUrl('/test');
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    expect(hostElement.querySelector<HTMLAnchorElement>('a').getAttribute('href').split('#')[0]).toEqual('/test');
  });
});
