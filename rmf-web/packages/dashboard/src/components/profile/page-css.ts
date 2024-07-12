import { styled } from '@mui/material';

export const profilePageClasses = {
  pageRoot: 'profile-pages-root',
  notFound: 'user-profile-page-notfound',
  manageRoles: 'user-profile-page-manageroles',
};
export const ProfilePageContainer = styled('div')(({ theme }) => ({
  [`&.${profilePageClasses.pageRoot}`]: {
    width: '84%',
    height: '100%',
    boxSizing: 'border-box',
    marginLeft: 'auto',
    padding: theme.spacing(4),
    backgroundColor: theme.palette.background.paper,
  },
  [`& .${profilePageClasses.notFound}`]: {
    marginTop: '50%',
    textAlign: 'center',
  },
  [`& .${profilePageClasses.manageRoles}`]: {
    marginTop: theme.spacing(4),
  },
}));
