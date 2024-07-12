import { Typography } from '@mui/material';
import { User } from 'api-client';
import { AxiosError } from 'axios';
import React from 'react';
import { useAsync } from 'react-components';
import { RmfAppContext } from '../rmf-app';
import { getApiErrorMessage } from '../utils';
import { ManageRolesCard } from './manage-roles-dialog';
import { profilePageClasses, ProfilePageContainer } from './page-css';
import { UserProfileCard } from './user-profile';
import { UpdatePasswordDto } from './update-user-api';

export function UserProfilePage(): JSX.Element | null {
  const safeAsync = useAsync();
  const { defaultApi, updateUserApi } = React.useContext(RmfAppContext) || {};
  const [user, setUser] = React.useState<User | undefined>(undefined);
  const [notFound, setNotFound] = React.useState(false);

  const refresh = React.useCallback(() => {
    if (!defaultApi) return;
    (async () => {
      try {
        setUser((await safeAsync(defaultApi.getUserUserGet())).data);
      } catch (e) {
        if ((e as AxiosError).response?.status !== 404) {
          throw new Error(getApiErrorMessage(e));
        }
        setNotFound(true);
      }
    })();
  }, [defaultApi, safeAsync]);

  React.useEffect(() => {
    refresh();
  }, [refresh]);

  return defaultApi ? (
    <ProfilePageContainer className={profilePageClasses.pageRoot}>
      {notFound ? (
        <Typography variant="h6" className={profilePageClasses.notFound}>
          404 Not Found
        </Typography>
      ) : (
        user && (
          <>
            <UserProfileCard
              user={user}
              updateEmail={(newEmail: string) => {
                // TODO: implement
              }}
              updatePassword={async (updatePasswordDto: UpdatePasswordDto) => {
                if (!updateUserApi) return false;
                await updateUserApi.updatePassword(updatePasswordDto);
              }}
            />
            <ManageRolesCard
              className={profilePageClasses.manageRoles}
              assignedRoles={user.roles}
            />
          </>
        )
      )}
    </ProfilePageContainer>
  ) : null;
}
