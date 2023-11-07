import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { FallbackEmissionsGroupComponent } from './fallback-emissions-group.component';

describe('FallbackEmissionsGroupComponent', () => {
  let page: Page;
  let component: FallbackEmissionsGroupComponent;
  let fixture: ComponentFixture<FallbackEmissionsGroupComponent>;

  class Page extends BasePage<FallbackEmissionsGroupComponent> {
    get summaryList(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FallbackEmissionsGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FallbackEmissionsGroupComponent);
    component = fixture.componentInstance;
    component.fallbackEmissions = {
      type: 'FALLBACK',
      sourceStreams: ['16776806322430.22129562351585452', '16842482180660.2528398674113044'],
      biomass: {
        contains: true,
        totalSustainableBiomassEmissions: '3.3',
        totalEnergyContentFromBiomass: '4.4',
        totalNonSustainableBiomassEmissions: '8.8',
      },
      description: 'asdf',
      files: ['ddc683ce-bc24-4ce5-8f5f-553fafc1baa8', 'e3c2ddba-053a-4486-a232-c6f983befc12'],
      totalFossilEmissions: '1.1',
      totalFossilEnergyContent: '2.2',
      reportableEmissions: '9.9',
    };
    component.sourceStreams = ['F1 Coke', 'S1 Acetylene'];
    component.documentFiles = [
      { downloadUrl: '/tasks/1/file-download/ddc683ce-bc24-4ce5-8f5f-553fafc1baa8', fileName: '100.bmp' },
      { downloadUrl: '/tasks/1/file-download/e3c2ddba-053a-4486-a232-c6f983befc12', fileName: '300.png' },
    ];

    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.summaryList).toEqual([
      'Source streams',
      'F1 Coke,S1 Acetylene',
      'Does any of the selected source streams contain biomass?',
      'Yes',
      'Description of the fallback approach',
      'asdf',
      'Document upload',
      '100.bmp300.png',
      'Fossil emissions',
      '1.1 tonnes CO2e',
      'Fossil energy content',
      '2.2 terajoules',
      'Sustainable biomass emissions',
      '3.3 tonnes CO2e',
      'Energy content from biomass',
      '4.4 terajoules',
      'Non sustainable biomass emissions',
      '8.8 tonnes CO2e',
    ]);
  });
});
