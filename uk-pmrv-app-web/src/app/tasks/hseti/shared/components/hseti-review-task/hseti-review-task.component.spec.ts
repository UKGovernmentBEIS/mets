import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { HsetiTaskReviewComponent } from './hseti-review-task.component';

describe('HsetiTaskReviewComponent', () => {
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
      <app-hseti-task-review [breadcrumb]="true" heading="Check your answers">
        <h2 class="govuk-heading-m">2021-2025 HSE target increase details</h2>
      </app-hseti-task-review>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HsetiTaskReviewComponent],
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
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Check your answers');

    expect(page.headings).toHaveLength(1);
    expect(page.headings[0].textContent.trim()).toEqual('2021-2025 HSE target increase details');
  });
});
