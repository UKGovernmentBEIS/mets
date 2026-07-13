import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { LinkDirective } from '../../directives';
import { NavListLegacyComponent } from './nav-list-legacy.component';

describe('NavListLegacyComponent', () => {
  @Component({
    imports: [NavListLegacyComponent, LinkDirective],
    template: `
      <govuk-header-nav-list-legacy
        ariaLabel="Aria label for test navigation"
        identifier="testNavigation"
        menuButtonAriaLabel="Aria label for menu button">
        <a govukLink="header" href="/">Test Link</a>
      </govuk-header-nav-list-legacy>
    `,
  })
  class HeaderNavListTestComponent {}

  let component: NavListLegacyComponent;
  let fixture: ComponentFixture<HeaderNavListTestComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderNavListTestComponent);
    component = fixture.debugElement.query(By.directive(NavListLegacyComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain menu button', () => {
    expect(element.querySelector('button').innerHTML.trim()).toEqual('Menu');
  });

  it('should contain menu button with responsive class', () => {
    expect(element.querySelector('button').classList).toContain('govuk-header-legacy__menu-button');
  });

  it('should contain an unordered list with govuk classes', () => {
    expect(element.querySelector('ul')).toBeTruthy();
    expect(element.querySelector('ul').className).toEqual('govuk-header-legacy__navigation-list');
  });

  it('should contain an anchor nav link', () => {
    expect(element.querySelector('a')).toBeTruthy();
    expect(element.querySelector('a').innerHTML).toEqual('Test Link');
  });

  it('should show navigation on button click', () => {
    const menuButton = fixture.nativeElement.querySelector('button');
    const navigationList = fixture.nativeElement.querySelector('ul');

    expect(menuButton.getAttribute('aria-expanded')).toEqual('false');

    menuButton.click();
    fixture.detectChanges();

    expect(menuButton.getAttribute('aria-expanded')).toEqual('true');
    expect(menuButton.classList).toContain('govuk-header-legacy__menu-button--open');
    expect(navigationList.classList).toContain('govuk-header-legacy__navigation--open');
  });
});
