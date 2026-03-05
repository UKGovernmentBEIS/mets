import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BasePage } from '@testing';

import { ChangesRequestedTemplateComponent } from './changes-requested-template.component';

describe('ChangesRequestedTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value div');
    }
  }
  @Component({
    template: `
      <app-changes-requested-template
        [requiredChanges]="requiredChanges"
        [reviewAttachments]="regulatorReviewAttachments"
        [downloadUrl]="downloadUrl"></app-changes-requested-template>
    `,
  })
  class TestComponent {
    requiredChanges = [{ reason: 'sdfsdf', files: ['2c63fda6-131b-49d7-8fe2-c64b54be2727'] }];
    regulatorReviewAttachments = { '2c63fda6-131b-49d7-8fe2-c64b54be2727': 'test_file1.txt' };
    downloadUrl = '/tasks/1/file-download/';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [ChangesRequestedTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['1. sdfsdf  test_file1.txt']);
  });
});
