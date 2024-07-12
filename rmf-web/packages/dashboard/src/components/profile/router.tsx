import { Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { ProfileDrawer } from './drawer';
import { UserProfilePage } from './user-profile-page';
import { RoleListPage } from './role-list-page';

export function ProfileRouter(): JSX.Element {
  return (
    <>
      <ProfileDrawer />
      <Routes>
        <Route path={'/*'} element={<Navigate to={'user'} />} />
        <Route path={'user'} element={<UserProfilePage />} />
        <Route path={'roles'} element={<RoleListPage />} />
        <Route element={<Navigate to={'user'} />} />
      </Routes>
      <Outlet />
    </>
  );
}
