import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmittedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { ALRApplicationProceededToAuthorityRequestActionPayload } from 'pmrv-api';

import { AlrAlcInformationSubmittedComponent } from './alc-information-submitted.component';

describe('AlcInformationSubmittedComponent', () => {
  let component: AlrAlcInformationSubmittedComponent;
  let fixture: ComponentFixture<AlrAlcInformationSubmittedComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<AlrAlcInformationSubmittedComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get historicalActivityLevelData() {
      return this.getActivityLevelTable(0);
    }

    get activityLevelData() {
      return this.getActivityLevelTable(1);
    }

    private getActivityLevelTable(idx: number) {
      return Array.from(this.queryAll<HTMLTableRowElement>('table')[idx].querySelectorAll('tr'))
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }

    get preliminaryAllocationsData() {
      return this.queryAll<HTMLTableRowElement>('app-alr-allocation-list-template table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAlcInformationSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_PROCEEDED_TO_AUTHORITY',
        submitter: '123',
        payload: {
          ...alrSubmittedRequestActionPayload,
          regulatorReviewOutcome: {
            conservativeDeterminesActivity: false,
            historicalActivityLevels: [
              {
                year: 2023,
                subInstallationName: 'ADIPIC_ACID',
                changeType: 'CESSATION',
                changedActivityLevel: '15.55%',
                comments: 'comment',
                creationDate: '2025-07-02T18:55:22Z',
              },
            ],
            activityLevels: [
              {
                year: 2022,
                subInstallationName: 'DOLIME',
                changeType: 'REGULATOR_REJECTS_ADJUSTMENT',
                changedActivityLevel: '11.55',
                comments: 'Comments 1',
                activityLevelChangeId: '0',
              },
              {
                year: 2023,
                subInstallationName: 'FACING_BRICKS',
                changeType: 'CESSATION',
                changedActivityLevel: '43.33',
                comments: 'Comments 2',
                activityLevelChangeId: '1',
              },
            ],
          },
        } as unknown as ALRApplicationProceededToAuthorityRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrAlcInformationSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['No']);
  });

  it('should display activity levels', () => {
    expect(page.activityLevelData).toEqual([
      ['2022', 'Dolime', 'Regulator rejects adjustment', '11.55', 'Comments 1'],
      ['2023', 'Facing bricks', 'Cessation', '43.33', 'Comments 2'],
    ]);
  });

  it('should display preliminary allocations', () => {
    expect(page.preliminaryAllocationsData).toEqual([]);
  });
});
