import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { FuelsGroupComponent } from './fuels-group.component';

describe('FuelsGroupComponent', () => {
  let component: FuelsGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: CommonTasksStore;
  let page: Page;

  @Component({
    template: `
      <app-fuels-group [aerData]="aerData"></app-fuels-group>
    `,
  })
  class TestComponent {
    aerData = {
      ...(mockState.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload).aer,
      emissionPoints: [
        {
          id: '333',
          reference: 'LL TK',
          description: 'Tunnel Kiln exhaust stack',
        },
        {
          id: '334',
          reference: 'LL BD 1-7',
          description: 'Brick Dryer exhausts 1-7',
        },
      ],
    };
  }

  class Page extends BasePage<TestComponent> {
    get sourceStreams() {
      return this.tableContent('Source streams (fuels and materials)');
    }
    get emissionSources() {
      return this.tableContent('Emission sources');
    }
    get emissionPoints() {
      return this.tableContent('Emission points');
    }

    tableContent(selector: string) {
      return Array.from(
        this.queryAll('li')
          .find((li) => li.textContent.trim().startsWith(selector))
          .querySelectorAll<HTMLTableRowElement>('tbody tr'),
      ).map((tr) => Array.from(tr.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);
    component = fixture.debugElement.query(By.directive(FuelsGroupComponent)).componentInstance;
    fixture.detectChanges();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild({ sourceStreams: null, emissionSources: null, emissionPoints: null }));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display source streams, emission sources and emission points', () => {
    expect(page.sourceStreams).toEqual([
      ['the reference', 'Anthracite', 'Ammonia: Fuel as process input'],
      ['the other reference', 'Biodiesels', 'Cement clinker: CKD'],
    ]);

    expect(page.emissionSources).toEqual([
      ['emission source 1 reference', 'emission source 1 description'],
      ['emission source 2 reference', 'emission source 2 description'],
    ]);

    expect(page.emissionPoints).toEqual([
      ['LL TK', 'Tunnel Kiln exhaust stack'],
      ['LL BD 1-7', 'Brick Dryer exhausts 1-7'],
    ]);
  });
});
