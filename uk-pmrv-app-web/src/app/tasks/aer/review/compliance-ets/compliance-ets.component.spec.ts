import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { ComplianceEtsComponent } from '@tasks/aer/review/compliance-ets/compliance-ets.component';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('ComplianceEtsComponent', () => {
  let component: ComplianceEtsComponent;
  let fixture: ComponentFixture<ComplianceEtsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'ETS_COMPLIANCE_RULES',
    },
  );

  class Page extends BasePage<ComplianceEtsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get sections(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('dl');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ComplianceEtsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Compliance with ETS rules');
    expect(
      page.sections.map((permit) => Array.from(permit.querySelectorAll('dt')).map((dd) => dd.textContent.trim())),
    ).toEqual([
      [
        'Monitoring Plan requirements met?',
        'EU Regulation on Monitoring and reporting met?',
        'Data verified in detail and back to source',
        'Control activities are documented, implemented, maintained and effective to mitigate the inherent risks?',
        'Procedures listed in monitoring plan are documented, implemented, maintained and effective to mitigate the inherent risks and control risks?',
        'Data verification completed as required?',
        'Correct application of monitoring methodology?',
        'Reporting of planned or actual changes?',
        'Verification of methods applied for missing data?',
        'Does the operator need to comply with uncertainty thresholds?',
        'Competent authority guidance on monitoring and reporting met?',
        'Previous year Non-Conformities corrected?',
      ],
      ['Decision status', 'Notes'],
    ]);
  });
});
