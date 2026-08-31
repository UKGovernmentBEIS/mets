import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { NerTaskComponent } from './ner-task.component';

describe('NerTaskComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get links(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a');
    }

    get pageheadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }

    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  @Component({
    standalone: false,
    template: `
      <app-ner-task heading="Check your answers" caption="New entrant reserve" taskType="NER_APPLICATION_SUBMIT">
        <app-summary-header changeRoute=".." class="govuk-heading-m">Check your answers</app-summary-header>
      </app-ner-task>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerTaskComponent, SharedModule],
      providers: [provideRouter([])],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all internal links', () => {
    const links = page.links;

    expect(links).toHaveLength(2);
    expect(links[0].textContent.trim()).toEqual('Change');
  });

  it('should display all internal titles', () => {
    expect(page.pageheadings).toHaveLength(1);
    expect(page.pageheadings[0].textContent.trim()).toEqual('Check your answers');

    const pageHeadings = page.headings;

    expect(page.headings).toHaveLength(1);
    expect(pageHeadings[0].textContent.trim()).toEqual('Check your answers');
  });
});
