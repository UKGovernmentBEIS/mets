import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { RequestActionDTO } from 'pmrv-api';

import { ActionSharedModule } from '../../action-shared-module';

describe('ActionLayoutComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <app-action-layout [header]="header" [requestAction]="requestActionItem"> </app-action-layout> `,
  })
  class TestComponent {
    header = 'Emissions report';
    requestActionItem: RequestActionDTO = {
      type: 'AER_APPLICATION_SUBMITTED',
      creationDate: '2020-12-21T13:42:43.050682Z',
    };
  }

  class Page extends BasePage<TestComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get creationDate(): string {
      return this.query('.govuk-caption-m').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, ActionSharedModule, RouterTestingModule],
      declarations: [TestComponent],
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

  it('should show appropriate content', () => {
    expect(page.heading).toEqual('Emissions report');
    expect(page.creationDate).toEqual('21 Dec 2020, 1:42pm');
  });
});
