import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { BdrS2TaskComponent } from './bdrs2-task.component';

describe('BdrS2TaskComponent', () => {
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
      <app-bdrs2-task [breadcrumb]="true">
        <app-page-heading caption="Provide the baseline data report and details">
          Free allocation and emitter status
        </app-page-heading>
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">Are you applying for HSE or USE status?</h2>
      </app-bdrs2-task>
    `,
  })
  class TestComponent {
    breadcrumb;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, BdrS2TaskComponent],
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
      jest.spyOn(store, 'requestTaskType$', 'get').mockReturnValue(of('BDRS2_APPLICATION_SUBMIT'));
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
      expect(page.pageheadings[0].textContent.trim()).toEqual('Free allocation and emitter status');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Are you applying for HSE or USE status? Change');
    });
  });
});
