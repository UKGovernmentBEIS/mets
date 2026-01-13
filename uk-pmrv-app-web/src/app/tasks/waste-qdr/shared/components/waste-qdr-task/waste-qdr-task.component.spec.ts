import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { WasteQdrTaskComponent } from './waste-qdr-task.component';

describe('WasteQdrTaskComponent', () => {
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
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
      <app-waste-qdr-task heading="Check your answers" caption="Quarterly data report" [breadcrumb]="true">
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">Check your answers</h2>
      </app-waste-qdr-task>
    `,
  })
  class TestComponent {
    breadcrumb: any;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrTaskComponent, SharedModule, TaskSharedModule],
      providers: [provideRouter([])],
      declarations: [TestComponent],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'requestTaskType$', 'get').mockReturnValue(of('WASTE_QDR_APPLICATION_SUBMIT'));

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
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
    expect(page.pageheadings[0].textContent.trim()).toEqual('Check your answers');

    const pageHeadings = page.headings;

    expect(page.headings).toHaveLength(1);
    expect(pageHeadings[0].textContent.trim()).toEqual('Check your answers Change');
  });
});
