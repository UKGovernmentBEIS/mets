import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ServiceNavigationComponent } from './service-navigation.component';

describe('ServiceNavigationComponent', () => {
  let component: ServiceNavigationComponent;
  let fixture: ComponentFixture<ServiceNavigationComponent>;

  beforeAll(() => {
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: jest.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // deprecated
        removeListener: jest.fn(), // deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
      })),
    });
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceNavigationComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ServiceNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default input values', () => {
    expect(component.ariaLabel()).toBe('Service information');
    expect(component.menuButtonText()).toBe('Menu');
    expect(component.menuButtonLabel()).toBe('Menu');
    expect(component.navigationId()).toBe('navigation');
    expect(component.navigationLabel()).toBe('Menu');
    expect(component.collapseNavigationOnMobile()).toBe(true);
  });

  it('should render the service name as text when serviceUrl is not provided', () => {
    fixture.componentRef.setInput('serviceName', 'Test Service');
    fixture.detectChanges();

    const serviceNameElement = fixture.nativeElement.querySelector('.govuk-service-navigation__service-name');
    expect(serviceNameElement.textContent).toContain('Test Service');
    expect(serviceNameElement.querySelector('a')).toBeNull();
  });

  it('should render the service name as a link when serviceUrl is provided', () => {
    fixture.componentRef.setInput('serviceName', 'Test Service');
    fixture.componentRef.setInput('serviceUrl', '/home');
    fixture.detectChanges();

    const serviceNameLink = fixture.nativeElement.querySelector('.govuk-service-navigation__service-name a');
    expect(serviceNameLink.textContent).toContain('Test Service');
    expect(serviceNameLink.getAttribute('href')).toBe('/home');
  });

  it('should render navigation items when provided', () => {
    fixture.componentRef.setInput('navigationItems', [
      { href: '/page-1', text: 'Page 1' },
      { href: '/page-2', text: 'Page 2' },
    ]);
    fixture.detectChanges();

    const navigationItems = fixture.nativeElement.querySelectorAll('.govuk-service-navigation__item');
    expect(navigationItems.length).toBe(2);
    expect(navigationItems[0].textContent).toContain('Page 1');
    expect(navigationItems[1].textContent).toContain('Page 2');

    const navigationLinks = fixture.nativeElement.querySelectorAll('.govuk-service-navigation__link');
    // First link is service name (if provided), followed by navigation items
    // In this test, serviceName is not provided, so only 2 links for navigationItems
    expect(navigationLinks.length).toBe(2);
    expect(navigationLinks[0].getAttribute('href')).toBe('/page-1');
    expect(navigationLinks[1].getAttribute('href')).toBe('/page-2');
  });

  it('should toggle the menu when toggleMenu is called', () => {
    expect(component.isMenuOpen()).toBe(false);
    component.toggleMenu();
    expect(component.isMenuOpen()).toBe(true);
    component.toggleMenu();
    expect(component.isMenuOpen()).toBe(false);
  });

  it('should show the menu button on mobile when collapseNavigationOnMobile is true', () => {
    fixture.componentRef.setInput('navigationItems', [{ href: '/page-1', text: 'Page 1' }]);
    fixture.componentRef.setInput('collapseNavigationOnMobile', true);
    // Mock isDesktop to return false
    jest.spyOn(component, 'isDesktop').mockReturnValue(false);
    fixture.detectChanges();

    const menuButton = fixture.nativeElement.querySelector('.govuk-service-navigation__toggle');
    expect(menuButton).toBeTruthy();
    expect(menuButton.textContent).toContain('Menu');
  });

  it('should update aria-expanded on the menu button when toggled', () => {
    fixture.componentRef.setInput('navigationItems', [{ href: '/page-1', text: 'Page 1' }]);
    jest.spyOn(component, 'isDesktop').mockReturnValue(false);
    fixture.detectChanges();

    const menuButton = fixture.nativeElement.querySelector('.govuk-service-navigation__toggle');
    expect(menuButton.getAttribute('aria-expanded')).toBe('false');

    component.toggleMenu();
    fixture.detectChanges();
    expect(menuButton.getAttribute('aria-expanded')).toBe('true');
  });
});
