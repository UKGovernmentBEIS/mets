import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default homepageUrl', () => {
    expect(component.homepageUrl()).toBe('https://www.gov.uk');
  });

  it('should detect external homepageUrl', () => {
    fixture.componentRef.setInput('homepageUrl', 'https://example.com');
    expect(component.isExternal()).toBe(true);

    fixture.componentRef.setInput('homepageUrl', 'http://example.com');
    expect(component.isExternal()).toBe(true);
  });

  it('should detect internal homepageUrl', () => {
    fixture.componentRef.setInput('homepageUrl', '/home');
    expect(component.isExternal()).toBe(false);

    fixture.componentRef.setInput('homepageUrl', 'home');
    expect(component.isExternal()).toBe(false);
  });

  it('should render the product name when provided', () => {
    fixture.componentRef.setInput('productName', 'Test Product');
    fixture.detectChanges();

    const productNameElement = fixture.nativeElement.querySelector('.govuk-header__product-name');
    expect(productNameElement.textContent).toContain('Test Product');
  });

  it('should not render the product name when not provided', () => {
    const productNameElement = fixture.nativeElement.querySelector('.govuk-header__product-name');
    expect(productNameElement).toBeNull();
  });

  it('should use an anchor tag with [href] for external homepageUrl', () => {
    fixture.componentRef.setInput('homepageUrl', 'https://www.gov.uk');
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('.govuk-header__homepage-link');
    expect(link.tagName).toBe('A');
    expect(link.getAttribute('href')).toBe('https://www.gov.uk');
  });

  it('should use an anchor tag with [routerLink] for internal homepageUrl', () => {
    fixture.componentRef.setInput('homepageUrl', '/internal');
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('.govuk-header__homepage-link');
    expect(link.tagName).toBe('A');
    expect(link.getAttribute('href')).toBe('/internal');
  });
});
