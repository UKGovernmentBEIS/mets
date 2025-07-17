import { ChangeDetectorRef, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { PermitTransferAModule } from '@tasks/permit-transfer-a/permit-transfer-a.module';
import { mockTransferPayload } from '@tasks/permit-transfer-a/testing/mock-state';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { PermitTransferDetailsSummaryTemplateComponent } from './transfer-details-summary-template.component';

describe('SummaryComponent', () => {
  let templateComponent: PermitTransferDetailsSummaryTemplateComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  const govukDatePipe = new GovukDatePipe();

  const runOnPushChangeDetection = async (fixture: ComponentFixture<any>): Promise<void> => {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  };

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0], row.querySelectorAll('dd')[1]])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  @Component({
    template: `
      <app-permit-transfer-details-summary-template
        [allowChange]="isEditable$ | async"
        [payload]="payload"></app-permit-transfer-details-summary-template>
    `,
  })
  class TestComponent {
    isEditable$ = new BehaviorSubject(true);
    payload: PermitTransferAApplicationRequestTaskPayload = { ...mockTransferPayload };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, PermitTransferDetailsSummaryTemplateComponent],
      imports: [PermitTransferAModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    templateComponent = fixture.debugElement.query(
      By.directive(PermitTransferDetailsSummaryTemplateComponent),
    ).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(templateComponent).toBeTruthy();
  });

  it('should display the editable payment summary', () => {
    expect(page.summaryListValues).toEqual([
      ['Reason for the transfer', 'Reason of transfer', 'Change'],
      ['Files added', 'None', 'Change'],
      ['Actual or estimated date of transfer', govukDatePipe.transform(mockTransferPayload.transferDate), 'Change'],
      ['Who is paying for the transfer?', 'Receiver', 'Change'],
      ['Who is completing the annual emission report?', 'Transferer', 'Change'],
      ['Transfer code', '123456789', 'Change'],
    ]);
  });

  it('should display the non editable payment summary', async () => {
    testComponent.isEditable$.next(false);
    await runOnPushChangeDetection(fixture);

    expect(page.summaryListValues).toEqual([
      ['Reason for the transfer', 'Reason of transfer', ''],
      ['Files added', 'None', ''],
      ['Actual or estimated date of transfer', govukDatePipe.transform(mockTransferPayload.transferDate), ''],
      ['Who is paying for the transfer?', 'Receiver', ''],
      ['Who is completing the annual emission report?', 'Transferer', ''],
      ['Transfer code', '123456789', ''],
    ]);
  });
});
