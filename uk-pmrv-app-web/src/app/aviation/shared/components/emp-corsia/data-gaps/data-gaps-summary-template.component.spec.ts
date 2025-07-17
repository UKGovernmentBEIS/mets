import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { render, RenderResult, screen } from '@testing-library/angular';

import { EmpDataGapsCorsia } from 'pmrv-api';

import { DataGapsCorsiaSummaryTemplateComponent } from './data-gaps-summary-template.component';

describe('DataGapsSummaryTemplate', () => {
  describe('DataGapsSummaryTemplate with FUM', () => {
    const dataGapsData: EmpDataGapsCorsia = {
      dataGaps: 'Data gaps description',
      secondaryDataSources: 'Secodary data sources description',
      secondarySourcesDataGapsExist: false,
    };
    let result: RenderResult<DataGapsCorsiaSummaryTemplateComponent>;
    beforeEach(async () => {
      result = await render(DataGapsCorsiaSummaryTemplateComponent, {
        imports: [RouterTestingModule, SharedModule, CommonModule],
        componentProperties: { isEditable: true, data: dataGapsData, corsiaMonitoringApproach: 'FUEL_USE_MONITORING' },
      });
      result.detectChanges();
    });

    it('should create', () => {
      const {
        fixture: { componentInstance: component },
      } = result;
      expect(component).toBeTruthy();
    });
    it('should render populated summary data', async () => {
      expect(screen.getByTestId('data-gaps-summary-template-fum')).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.dataGaps)).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.secondaryDataSources)).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.secondarySourcesDataGapsExist ? 'Yes' : 'No')).toBeInTheDocument();
      expect((await screen.findAllByText('Change')).length).toEqual(3);
    });
  });
  describe('DataGapsSummaryTemplate with CERT', () => {
    const dataGapsData: EmpDataGapsCorsia = {
      dataGaps: 'Data gaps description',
      secondaryDataSources: 'Secodary data sources description',
      secondarySourcesDataGapsExist: false,
    };
    let result: RenderResult<DataGapsCorsiaSummaryTemplateComponent>;
    beforeEach(async () => {
      result = await render(DataGapsCorsiaSummaryTemplateComponent, {
        imports: [RouterTestingModule, SharedModule, CommonModule],
        componentProperties: { isEditable: true, data: dataGapsData, corsiaMonitoringApproach: 'CERT_MONITORING' },
      });
      result.detectChanges();
    });

    it('should create', () => {
      const {
        fixture: { componentInstance: component },
      } = result;
      expect(component).toBeTruthy();
    });
    it('should render populated summary data', async () => {
      expect(screen.getByTestId('data-gaps-summary-template-cert')).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.dataGaps)).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.secondaryDataSources)).toBeInTheDocument();
      expect(screen.getByText(dataGapsData.secondarySourcesDataGapsExist ? 'Yes' : 'No')).toBeInTheDocument();
      expect((await screen.findAllByText('Change')).length).toEqual(3);
    });
  });
});
