import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { mockAirApplicationSubmitPayload } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { BasePage } from '@testing';

import { AirImprovementDataGroupComponent } from './air-improvement-data-group.component';

describe('AirImprovementDataGroupComponent', () => {
  let page: Page;
  let component: AirImprovementDataGroupComponent;
  let fixture: ComponentFixture<AirImprovementDataGroupComponent>;

  class Page extends BasePage<AirImprovementDataGroupComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, ul a, ul strong').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirImprovementDataGroupComponent],
      imports: [AirSharedModule, AirTaskSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirImprovementDataGroupComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.airPayload = {
      ...mockAirApplicationSubmitPayload,
      airSectionsCompleted: { '1': true, '2': false },
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Item 1: F1: Acetylene: major: emission factor',
      'Provide information about this improvement',
      'completed',
      'Item 2: F1: Acetylene: major: emission factor',
      'Provide information about this improvement',
      'in progress',
      'Item 3: EP1: West side chimney: major',
      'Provide information about this improvement',
      'not started',
      'Item 4: EP1: West side chimney: major',
      'Provide information about this improvement',
      'not started',
      'Item 5: F1: Acetylene: major',
      'Provide information about this improvement',
      'not started',
    ]);
  });
});
