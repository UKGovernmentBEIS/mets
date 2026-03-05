import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../../../../testing';
import { PermitNotificationSharedModule } from '../../../../../../shared/components/permit-notification/permit-notification-shared.module';
import { GovukDatePipe } from '../../../../../../shared/pipes/govuk-date.pipe';
import { SharedModule } from '../../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../../shared/task-shared-module';
import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  @Component({
    template: `
      <app-decision-summary [reviewDecision]="reviewDecision"></app-decision-summary>
    `,
  })
  class TestComponent {
    currentDate = new Date().toISOString();
    reviewDecision = { type: 'ACCEPTED', details: { dueDate: this.currentDate, notes: 'notes' } };
  }

  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLinks() {
      return this.queryAll<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(DecisionSummaryComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render summary ', () => {
    const govukDatePipe = new GovukDatePipe();
    const currentDate = govukDatePipe.transform(new Date().toISOString(), 'date');

    expect(page.rows).toEqual([
      ['Decision status', 'Accepted'],
      ['New due date for the response', currentDate],
      ['Notes', 'notes'],
    ]);
  });
});
