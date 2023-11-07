import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AlcInformationSummaryComponent } from './alc-information-summary.component';

describe('AlcInformationSummaryComponent', () => {
  let page: Page;
  let component: AlcInformationSummaryComponent;
  let fixture: ComponentFixture<AlcInformationSummaryComponent>;

  class Page extends BasePage<AlcInformationSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get appDoalAlcInformationTemplate() {
      return this.query('app-doal-alc-information-template');
    }

    get appReturnTaskLink() {
      return this.query('app-task-return-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AlcInformationSummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(AlcInformationSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskType$ = of('DOAL_WAIT_FOR_PEER_REVIEW');
    component.activityLevelChangeInformation$ = of({
      activityLevels: [
        {
          year: 2022,
          subInstallationName: 'ADIPIC_ACID',
          changeType: 'CESSATION',
          changedActivityLevel: '20',
          comments: 'Test comments',
        },
      ],
      areConservativeEstimates: true,
      explainEstimates: 'Test explain explainEstimates',
      preliminaryAllocations: [
        {
          subInstallationName: 'ALUMINIUM',
          year: 2022,
          allowances: 10,
        },
      ],
      commentsForUkEtsAuthority: 'Test commentsForUkEtsAuthority',
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Provide information about this activity level change');
    expect(page.appDoalAlcInformationTemplate).toBeTruthy();
    expect(page.appReturnTaskLink).toBeTruthy();
  });
});
