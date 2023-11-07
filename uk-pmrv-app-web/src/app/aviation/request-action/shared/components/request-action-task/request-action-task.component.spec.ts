import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

describe('RequestActionTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: RequestActionStore;

  const route = new ActivatedRouteStub({ actionId: 63 });

  class Page extends BasePage<TestComponent> {
    get links(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a');
    }

    get pageHeadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }

    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  @Component({
    template: `
      <app-request-action-task
        [breadcrumb]="true"
        header="Abbreviations and definitions"
        requestActionType="EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED"
      >
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">
          Uploaded additional documents and information
        </h2>
      </app-request-action-task>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, RequestActionTaskComponent],
      declarations: [TestComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
      },
      regulatorViewer: false,
    });

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
    expect(links[1].textContent.trim()).toEqual('Return to: Submitted');
  });

  it('should display all internal titles', () => {
    expect(page.pageHeadings).toHaveLength(1);
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Abbreviations and definitions');

    const pageHeadings = page.headings;

    expect(page.headings).toHaveLength(1);
    expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
  });
});
