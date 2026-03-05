import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../store/common-actions.store';
import { ActionSharedModule } from '../action-shared-module';
import { ActionTaskComponent } from './action-task.component';

describe('ActionTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: CommonActionsStore;

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Review follow up response to a notification',
  });

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
      <app-action-task [breadcrumb]="breadcrumb" header="Abbreviations and definitions">
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">
          Uploaded additional documents and information
        </h2>
      </app-action-task>
    `,
  })
  class TestComponent {
    breadcrumb: true | BreadcrumbItem[] = true;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, ActionSharedModule],
      declarations: [ActionTaskComponent, TestComponent],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  describe('for submit', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonActionsStore);
      jest.spyOn(store, 'requestActionType$', 'get').mockReturnValue(of('AER_APPLICATION_SUBMITTED'));
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
      expect(page.pageHeadings).toHaveLength(1);
      expect(page.pageHeadings[0].textContent.trim()).toEqual('Abbreviations and definitions');

      const pageHeadings = page.headings;

      expect(page.headings).toHaveLength(1);
      expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
    });
  });
});
