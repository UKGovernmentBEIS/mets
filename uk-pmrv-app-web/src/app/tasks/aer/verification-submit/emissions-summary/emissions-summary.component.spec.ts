import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { EmissionsSummaryComponent } from './emissions-summary.component';

describe('EmissionsSummaryComponent', () => {
  let component: EmissionsSummaryComponent;
  let fixture: ComponentFixture<EmissionsSummaryComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<EmissionsSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get emissionsCalculations() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionsCalculationsTextContents() {
      return this.emissionsCalculations.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(EmissionsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Emissions summary');
  });
});
