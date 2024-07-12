import { Route, Routes, Navigate, Outlet, useNavigate } from 'react-router-dom';
import { AdminDrawer } from './drawer';
import { RoleListPage } from './role-list-page';
import { UserListPage } from './user-list-page';
import { UserProfilePage } from './user-profile-page';
import React, { useEffect } from 'react';
import { UserProfileContext } from 'rmf-auth';
import { AppControllerContext } from '../app-contexts';

export function AdminRouter(): JSX.Element {
  const { showAlert } = React.useContext(AppControllerContext);
  const profile = React.useContext(UserProfileContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!profile?.user.is_admin) {
      navigate('/');
      showAlert("error", "해당 페이지에 접근할 권한이 없습니다.");
    }
  }, [showAlert, profile, navigate]);

  return (
    <>
      <AdminDrawer />
      <Routes>
        <Route path={'/*'} element={<Navigate to={'users'} />} />
        <Route path={'/users/:username'} element={<UserProfilePage />} />
        <Route path={'users'} element={<UserListPage />} />
        <Route path={'roles'} element={<RoleListPage />} />
        <Route element={<Navigate to={'users'} />} />
      </Routes>
      <Outlet />
    </>
  );
}
