import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { mockPaymentState } from '../../../testing/mock-state';
import { ReturnLinkComponent } from './return-link.component';

describe('ReturnLinkComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <app-return-link
        [home]="home"
        [requestType]="requestType"
        [requestTaskType]="requestTaskType"
        returnLink=".."></app-return-link>
    `,
  })
  class TestComponent {
    home = false;
    requestType = mockPaymentState.requestType;
    requestTaskType = mockPaymentState.requestTaskItem.requestTask.type;
  }

  class Page extends BasePage<TestComponent> {
    get link() {
      return this.query<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TestComponent, ReturnLinkComponent],
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

  it('should show link', () => {
    expect(page.link).toBeTruthy();
    expect(page.link.textContent.trim()).toEqual('Return to: Pay permit application fee');
  });
});
