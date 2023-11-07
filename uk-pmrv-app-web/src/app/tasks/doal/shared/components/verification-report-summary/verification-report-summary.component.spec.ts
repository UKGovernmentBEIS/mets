import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { VerificationReportSummaryComponent } from './verification-report-summary.component';

describe('VerificationReportSummaryComponent', () => {
  let page: Page;
  let component: VerificationReportSummaryComponent;
  let fixture: ComponentFixture<VerificationReportSummaryComponent>;

  class Page extends BasePage<VerificationReportSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get appVerificationReportSummaryTemplate() {
      return this.query('app-verification-report-summary-template');
    }

    get appReturnTaskLink() {
      return this.query('app-task-return-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationReportSummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationReportSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskType$ = of('DOAL_WAIT_FOR_PEER_REVIEW');
    component.verificationActivityLevelReport$ = of({
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
    expect(page.heading1.textContent.trim()).toEqual('Upload verification report of the activity level report');
    expect(page.appVerificationReportSummaryTemplate).toBeTruthy();
    expect(page.appReturnTaskLink).toBeTruthy();
  });
});
