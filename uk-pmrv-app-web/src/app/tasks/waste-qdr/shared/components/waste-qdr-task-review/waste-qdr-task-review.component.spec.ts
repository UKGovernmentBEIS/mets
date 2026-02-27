import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { WasteQdrTaskReviewComponent } from './waste-qdr-task-review.component';

describe('WasteQdrTaskReviewComponent', () => {
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
    standalone: false,
    template: `
      <app-waste-qdr-task-common [breadcrumb]="true" heading="Quarterly data report">
        <h2 class="govuk-heading-m">Sub header</h2>
      </app-waste-qdr-task-common>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrTaskReviewComponent],
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
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Quarterly data report');

    expect(page.headings).toHaveLength(1);
    expect(page.headings[0].textContent.trim()).toEqual('Sub header');
  });
});
