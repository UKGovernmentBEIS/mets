import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { DataGapsComponent } from '@tasks/aer/review/data-gaps/data-gaps.component';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('DataGapsComponent', () => {
  let component: DataGapsComponent;
  let fixture: ComponentFixture<DataGapsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'CLOSE_DATA_GAPS_METHODOLOGIES',
    },
  );

  class Page extends BasePage<DataGapsComponent> {
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

    fixture = TestBed.createComponent(DataGapsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Methodologies to close data gaps');
    expect(
      page.sections.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent.trim())),
    ).toEqual([['No'], ['Accepted', 'Notes']]);
  });
});
