import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { OperatorReportSummaryComponent } from './operator-report-summary.component';

describe('OperatorReportSummaryComponent', () => {
  let page: Page;
  let component: OperatorReportSummaryComponent;
  let fixture: ComponentFixture<OperatorReportSummaryComponent>;

  class Page extends BasePage<OperatorReportSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get appOperatorReportSummaryTemplate() {
      return this.query('app-operator-report-summary-template');
    }

    get appReturnTaskLink() {
      return this.query('app-task-return-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorReportSummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(OperatorReportSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskType$ = of('DOAL_WAIT_FOR_PEER_REVIEW');
    component.operatorActivityLevelReport$ = of({
      areActivityLevelsEstimated: true,
      comment: 'Test comment 1',
      document: '11111111-1111-4111-a111-111111111111',
    });
    component.documentFile$ = of({
      downloadUrl: '11111111-1111-4111-a111-111111111111',
      fileName: '100.bmp',
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Upload operator activity level report');
    expect(page.appOperatorReportSummaryTemplate).toBeTruthy();
    expect(page.appReturnTaskLink).toBeTruthy();
  });
});
