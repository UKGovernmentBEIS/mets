import { LegalEntity } from '@shared/interfaces/legal-entity';
import { TaskItem } from '@shared/task-list/task-list.interface';

import { ApplicationType } from '../application-type/application-type';
import { Commencement } from '../commencement/commencement';
import { Responsibility } from '../confirm-responsibility/responsibility';
import { EtsScheme } from '../ets-scheme/ets-scheme';
import { Installation } from '../installation-type/installation';

export enum ApplicationSectionType {
  responsibility,
  legalEntity,
  installation,
  etsScheme,
  commencement,
  applicationType,
}

export interface ResponsibilitySection extends TaskItem<ApplicationSectionType.responsibility> {
  title: string;
  value?: Responsibility;
}

export interface LegalEntitySection extends TaskItem<ApplicationSectionType.legalEntity> {
  title: string;
  value?: LegalEntity;
}

export interface InstallationSection extends TaskItem<ApplicationSectionType.installation> {
  title: string;
  value?: Installation;
}

export interface EtsSchemeSection extends TaskItem<ApplicationSectionType.etsScheme> {
  title: string;
  value?: EtsScheme;
}
export interface CommencementSection extends TaskItem<ApplicationSectionType.commencement> {
  title: string;
  value?: Commencement;
}
export interface ApplicationTypeSection extends TaskItem<ApplicationSectionType.applicationType> {
  title: string;
  value?: ApplicationType;
}

export type Section =
  | ResponsibilitySection
  | LegalEntitySection
  | InstallationSection
  | EtsSchemeSection
  | CommencementSection
  | ApplicationTypeSection;

export interface InstallationAccountApplicationState {
  tasks: Section[];
  isSummarized?: boolean;
  isReviewed?: boolean;
  taskId?: number;
  requestActionCreationDate?: string;
}

export const initialState: InstallationAccountApplicationState = {
  tasks: [
    {
      type: ApplicationSectionType.responsibility,
      title: 'Confirm responsibility for operating the installation',
      linkText: 'Installation operator responsibility',
      status: 'not started',
      link: 'responsibility',
    },
    {
      type: ApplicationSectionType.legalEntity,
      title: 'Add the installation operator details',
      linkText: 'Identity and contact details of the operator',
      status: 'not started',
      link: 'legal-entity-op',
    },
    {
      type: ApplicationSectionType.installation,
      title: 'Add the installation details',
      linkText: 'Installation details',
      status: 'not started',
      link: 'installation',
    },
    {
      type: ApplicationSectionType.etsScheme,
      title: 'Select the emissions trading scheme',
      linkText: 'ETS Scheme',
      status: 'not started',
      link: 'ets-scheme',
    },
    {
      type: ApplicationSectionType.commencement,
      title: 'Add the commencement date of operations',
      linkText: 'Indicate the date of commencement of operations',
      status: 'not started',
      link: 'commencement',
    },
    {
      type: ApplicationSectionType.applicationType,
      title: 'Confirm the type of application',
      linkText: 'Application type',
      status: 'not started',
      link: 'type',
    },
  ],
  isSummarized: false,
  isReviewed: false,
};
