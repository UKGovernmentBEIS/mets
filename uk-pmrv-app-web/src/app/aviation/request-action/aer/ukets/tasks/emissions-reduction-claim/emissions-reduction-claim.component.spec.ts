import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import EmissionsReductionClaimComponent from '@aviation/request-action/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<EmissionsReductionClaimComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
  get tableRows(): HTMLTableRowElement[] {
    return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
  }
}

describe('EmissionsReductionClaimComponent', () => {
  let component: EmissionsReductionClaimComponent;
  let fixture: ComponentFixture<EmissionsReductionClaimComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, EmissionsReductionClaimComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          aer: {
            saf: {
              exist: true,
              safDetails: {
                purchases: [
                  {
                    safMass: '12',
                    fuelName: 'Fuel name',
                    batchNumber: '12',
                    evidenceFiles: ['94c5222e-35db-49ef-a097-4e64d083e6f9', '02b269e5-c089-4565-a18c-0573dc7a3fd2'],
                    sustainabilityCriteriaEvidenceType: 'SCREENSHOT_FROM_RTFO_REGISTRY',
                  },
                ],
                totalSafMass: '12',
                emissionsFactor: '3.15',
                totalEmissionsReductionClaim: '37.8',
                noDoubleCountingDeclarationFile: '0def32eb-c5dd-437e-a9a1-b2f2b4c8ba50',
              },
            },
          },
          aerAttachments: {
            '0def32eb-c5dd-437e-a9a1-b2f2b4c8ba50': 'Declaration file',
            '94c5222e-35db-49ef-a097-4e64d083e6f9': 'File 1',
            '02b269e5-c089-4565-a18c-0573dc7a3fd2': 'File 2',
          },
        } as any,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(EmissionsReductionClaimComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Emissions reduction claim');
    expect(page.summaryValues).toEqual([
      ['Will you be making an emissions reduction claim as a result of the purchase and delivery of SAF?', 'Yes'],
      ['Declaration of no double counting', 'Declaration file'],
      ['Total mass of sustainable aviation fuel claimed', '12 tonnes'],
      ['Emissions factor applied', '3.15 tCO2 per tonne of fuel'],
      ['Total emissions reduction claim for the scheme Year', '37.8 tCO2'],
    ]);
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['Fuel name', '12', '12 t', 'File 1  File 2', ''],
    ]);
  });
});
