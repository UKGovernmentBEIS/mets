import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { DeterminationSummaryComponent } from './determination-summary.component';

describe('DeterminationSummaryComponent', () => {
  let page: Page;
  let component: DeterminationSummaryComponent;
  let fixture: ComponentFixture<DeterminationSummaryComponent>;

  class Page extends BasePage<DeterminationSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get appDeterminationSummaryTemplate() {
      return this.query('app-determination-summary-template');
    }

    get appReturnTaskLink() {
      return this.query('app-task-return-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeterminationSummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(DeterminationSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskType$ = of('DOAL_WAIT_FOR_PEER_REVIEW');
    component.determination$ = of({
      type: 'CLOSED',
      reason: 'Official notice',
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Provide determination of activity level');
    expect(page.appDeterminationSummaryTemplate).toBeTruthy();
    expect(page.appReturnTaskLink).toBeTruthy();
  });
});
