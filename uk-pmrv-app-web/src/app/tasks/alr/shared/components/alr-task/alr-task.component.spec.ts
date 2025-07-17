import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrTaskComponent } from './alr-task.component';

describe('AlrTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: CommonTasksStore;

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
    template: `
      <app-alr-task [breadcrumb]="true">
        <app-page-heading caption="Provide the activity level report">
          Upload the activity level report file
        </app-page-heading>
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">Uploaded activity level report</h2>
      </app-alr-task>
    `,
  })
  class TestComponent {
    breadcrumb;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AlrTaskComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  describe('for submit', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      jest.spyOn(store, 'requestTaskType$', 'get').mockReturnValue(of('ALR_APPLICATION_SUBMIT'));
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(hostComponent).toBeTruthy();
    });

    it('should display all internal links', () => {
      const links = page.links;

      expect(links).toHaveLength(2);
      expect(links[0].textContent.trim()).toEqual('Change');
    });

    it('should display all internal titles', () => {
      expect(page.pageheadings).toHaveLength(1);
      expect(page.pageheadings[0].textContent.trim()).toEqual('Upload the activity level report file');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded activity level report Change');
    });
  });
});
