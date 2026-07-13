import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { accordionFactory } from '../accordion';
import { AccordionComponent } from '../accordion.component';
import { AccordionItemSummaryDirective } from '../directives/accordion-item-summary.directive';
import { AccordionItemComponent } from './accordion-item.component';

describe('AccordionItemComponent', () => {
  let component: AccordionItemComponent;
  let fixture: ComponentFixture<TestComponent>;

  const getSectionButtons = (f: ComponentFixture<TestComponent>) =>
    f.nativeElement.querySelectorAll('.govuk-accordion__section-button');
  const getChevrons = (f: ComponentFixture<TestComponent>) =>
    f.nativeElement.querySelectorAll('.govuk-accordion-nav__chevron');
  const getSectionToggles = (f: ComponentFixture<TestComponent>) =>
    f.nativeElement.querySelectorAll('.govuk-accordion__section-toggle-text');

  @Component({
    imports: [AccordionComponent, AccordionItemComponent, AccordionItemSummaryDirective],
    standalone: true,
    template: `
      <govuk-accordion id="test-accordion" [openIndexes]="openIndexes">
        <govuk-accordion-item header="First item">
          <div govukAccordionItemSummary>First item summary</div>
          <p>Content</p>
        </govuk-accordion-item>
        <govuk-accordion-item header="Second item">
          <p>Content</p>
        </govuk-accordion-item>
      </govuk-accordion>
    `,
  })
  class TestComponent {
    openIndexes = [2];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [accordionFactory],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(AccordionItemComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have auto generated ids', () => {
    const headingButton = fixture.nativeElement.querySelector('.govuk-accordion__section-button');
    const headingSpan = fixture.nativeElement.querySelector('.govuk-accordion__section-heading-text');
    expect(headingButton).toBeTruthy();
    expect(headingSpan).toBeTruthy();
    expect(headingSpan.id).toEqual('test-accordion-heading-1');

    const summary = fixture.nativeElement.querySelector('.govuk-accordion__section-summary');
    expect(summary.id).toEqual('test-accordion-summary-1');

    const content = fixture.nativeElement.querySelector('.govuk-accordion__section-content');
    expect(content.id).toEqual('test-accordion-content-1');
  });

  it('should render content', () => {
    const contentDiv = fixture.nativeElement.querySelector('.govuk-accordion__section-content');
    expect(contentDiv.childNodes[0].tagName).toEqual('P');
    expect(contentDiv.childNodes[0].textContent).toEqual('Content');
  });

  it('should render focus spans', () => {
    const headerFocusSpan = fixture.nativeElement.querySelector('.govuk-accordion__section-heading-text-focus');
    expect(headerFocusSpan.textContent).toContain('First item');

    const summaryFocusSpan = fixture.nativeElement.querySelector('.govuk-accordion__section-summary-focus');
    expect(summaryFocusSpan.textContent).toContain('First item summary');

    const toggleFocusSpan = fixture.nativeElement.querySelector('.govuk-accordion__section-toggle');
    expect(toggleFocusSpan.childNodes[0].classList).toContain('govuk-accordion__section-toggle-focus');
  });

  it('should contain button with attributes', () => {
    const headingButtons = getSectionButtons(fixture);

    expect(headingButtons[0].getAttribute('aria-controls')).toEqual('test-accordion-content-1');
    expect(headingButtons[1].getAttribute('aria-controls')).toEqual('test-accordion-content-2');

    expect(headingButtons[0].getAttribute('aria-label')).toContain('First item , Show this section');
    headingButtons[0].click();
    fixture.detectChanges();
    expect(headingButtons[0].getAttribute('aria-label')).toContain('First item , Hide this section');
  });

  it('should initialize with open indexes', () => {
    const sectionButtons = getSectionButtons(fixture);

    expect(sectionButtons[0].getAttribute('aria-expanded')).toEqual('false');
    expect(sectionButtons[1].getAttribute('aria-expanded')).toEqual('true');
  });

  it('should expand item on section click', () => {
    const sectionButtons = getSectionButtons(fixture);

    sectionButtons[0].click();
    fixture.detectChanges();

    expect(sectionButtons[0].getAttribute('aria-expanded')).toEqual('true');
    expect(sectionButtons[1].getAttribute('aria-expanded')).toEqual('true');

    sectionButtons[1].click();
    fixture.detectChanges();

    expect(sectionButtons[0].getAttribute('aria-expanded')).toEqual('true');
    expect(sectionButtons[1].getAttribute('aria-expanded')).toEqual('false');

    sectionButtons[1].click();
    fixture.detectChanges();

    expect(sectionButtons[0].getAttribute('aria-expanded')).toEqual('true');
    expect(sectionButtons[1].getAttribute('aria-expanded')).toEqual('true');
  });

  it('should change chevron class on click', () => {
    const sectionButtons = getSectionButtons(fixture);
    const chevrons = getChevrons(fixture);
    const toggles = getSectionToggles(fixture);

    expect(chevrons[0].classList).toContain('govuk-accordion-nav__chevron--down');
    expect(chevrons[1].classList).toContain('govuk-accordion-nav__chevron--down');
    expect(chevrons[2].classList).not.toContain('govuk-accordion-nav__chevron--down');
    expect(toggles[1].textContent).toContain('Hide');

    sectionButtons[1].click();
    fixture.detectChanges();

    expect(chevrons[0].classList).toContain('govuk-accordion-nav__chevron--down');
    expect(chevrons[1].classList).toContain('govuk-accordion-nav__chevron--down');
    expect(chevrons[2].classList).toContain('govuk-accordion-nav__chevron--down');
    expect(toggles[1].textContent).toContain('Show');
  });
});
