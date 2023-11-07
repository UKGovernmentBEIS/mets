import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { BasePage } from '@testing';

import { AirImprovementItemComponent } from './air-improvement-item.component';

describe('AirImprovementItemComponent', () => {
  let page: Page;
  let component: AirImprovementItemComponent;
  let fixture: ComponentFixture<AirImprovementItemComponent>;

  class Page extends BasePage<AirImprovementItemComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirImprovementItemComponent],
      imports: [AirSharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AirImprovementItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.reference = '1';
    component.airImprovement = {
      type: 'CALCULATION_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      sourceStreamReferences: ['F1: Acetylene'],
      emissionPoint: 'EP1: West side chimney',
      emissionPoints: ['EP1: West side chimney'],
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementAll;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Item 1: F1: Acetylene: major: emission factor',
      'Monitoring approach',
      'Calculation of CO2',
      '',
      'Source stream category',
      'Major',
      '',
      'Source stream reference',
      'F1: Acetylene',
      '',
      'Source stream reference',
      'F1: Acetylene',
      '',
      'Emission sources',
      'S1: Boiler',
      '',
      'Emission points',
      'EP1: West side chimney',
      '',
      'Emission points',
      'EP1: West side chimney',
      '',
      'Parameter',
      'Emission factor',
      '',
      'Currently applied tier',
      'Tier 1',
      '',
    ]);
  });
});
