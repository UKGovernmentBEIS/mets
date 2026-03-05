import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { BdrTaskReviewComponent } from './bdr-task-review.component';

describe('BdrTaskReviewComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  class Page extends BasePage<TestComponent> {
    get pageHeadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }
    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
    get links(): HTMLLinkElement {
      return this.query<HTMLLinkElement>('a');
    }
  }

  @Component({
    template: `
      <app-bdr-task-review [breadcrumb]="true" heading="Verify baseline data report">
        <h2 class="govuk-heading-m">Baseline data report and details</h2>
      </app-bdr-task-review>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrTaskReviewComponent],
      declarations: [TestComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should display all internal titles', () => {
    expect(page.pageHeadings).toHaveLength(1);
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Verify baseline data report');

    expect(page.headings).toHaveLength(1);
    expect(page.headings[0].textContent.trim()).toEqual('Baseline data report and details');
  });
});
