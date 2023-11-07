import { GovukTableColumn } from 'govuk-components';

import { ItemDTO } from 'pmrv-api';

export const columns: GovukTableColumn<ItemDTO>[] = [
  { field: 'taskType', header: 'Task', isSortable: true },
  { field: 'taskAssignee', header: 'Assigned to', isSortable: true },
  { field: 'daysRemaining', header: 'Days remaining', isSortable: true },
  { field: 'permitReferenceId', header: 'Permit ID', isSortable: true },
  { field: 'accountName', header: 'Installation', isSortable: true },
  { field: 'leName', header: `Operator`, isSortable: true },
];

export const assignedItems: ItemDTO[] = [
  {
    taskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 22,
    permitReferenceId: 'PERM_REF_ID_1',
    accountName: 'ACCOUNT_1',
    leName: 'LE_1',
  },
  {
    taskType: 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 12,
    permitReferenceId: 'PERM_REF_ID_2',
    accountName: 'ACCOUNT_2',
    leName: 'LE_2',
  },
  {
    taskType: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 10,
    permitReferenceId: 'PERM_REF_ID_3',
    accountName: 'ACCOUNT_3',
    leName: 'LE_3',
  },
  {
    taskType: 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW',
    taskAssignee: { firstName: 'TEST_FN', lastName: 'TEST_LN' },
    daysRemaining: 16,
    permitReferenceId: 'PERM_REF_ID_4',
    accountName: 'ACCOUNT_4',
    leName: 'LE_4',
  },
];

export const unassignedItems = assignedItems.map((item) => ({ ...item, taskAssignee: null }));
