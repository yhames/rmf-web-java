import React from 'react';
import { RmfAppContext } from '../rmf-app';
import { getApiErrorMessage } from '../utils';
import { profilePageClasses, ProfilePageContainer } from './page-css';
import { RoleListCard } from './role-list-card';

export function RoleListPage(): JSX.Element | null {
  const rmfIngress = React.useContext(RmfAppContext);
  const adminApi = rmfIngress?.adminApi;

  if (!adminApi) return null;

  return (
    <ProfilePageContainer className={profilePageClasses.pageRoot}>
      <RoleListCard
        getRoles={async () => (await adminApi.getRolesAdminRolesGet()).data}
        getPermissions={async (role) => {
          try {
            return (await adminApi.getRolePermissionsAdminRolesRolePermissionsGet(role)).data;
          } catch (e) {
            throw new Error(getApiErrorMessage(e));
          }
        }}
      />
    </ProfilePageContainer>
  );
}
