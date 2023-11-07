import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestActionDTO } from 'pmrv-api';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { DoalActionTaskComponent } from './doal-action-task.component';

describe('ActionTaskComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

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
      <app-doal-action-task [header]="header" [actionType]="actionType">
        <h2 app-summary-header changeRoute=".." class="govuk-heading-m">
          Uploaded additional documents and information
        </h2>
      </app-doal-action-task>
    `,
  })
  class TestComponent {
    header = 'My Header';
    actionType: RequestActionDTO['type'] = 'DOAL_APPLICATION_ACCEPTED';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, ActionSharedModule],
      declarations: [DoalActionTaskComponent, TestComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.nativeElement;
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
    expect(links[1].textContent.trim()).toEqual('Return to: Activity level determination accepted as approved');
  });

  it('should display all internal titles', () => {
    expect(page.pageHeadings).toHaveLength(1);
    expect(page.pageHeadings[0].textContent.trim()).toEqual('My Header');

    const pageHeadings = page.headings;

    expect(page.headings).toHaveLength(1);
    expect(pageHeadings[0].textContent.trim()).toEqual('Uploaded additional documents and information  Change');
  });
});
