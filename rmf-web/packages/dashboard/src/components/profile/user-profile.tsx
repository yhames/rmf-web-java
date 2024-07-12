import {
  Button,
  Card,
  CardHeader,
  CardContent,
  CardProps,
  Grid,
  styled,
  Typography,
} from '@mui/material';
import AccountIcon from '@mui/icons-material/AccountCircle';
import { User } from 'api-client';
import React from 'react';
import { UpdateEmailDialog } from './update-email-dialog';
import { UpdatePasswordDialog } from './update-password-dialog';
import { UpdatePasswordDto } from './update-user-api';

const classes = {
  avatar: 'user-profile-action',
  button: 'user-edit-button',
};

const StyledCard = styled((props: CardProps) => <Card {...props} />)(({ theme }) => ({
  [`& .${classes.avatar}`]: {
    color: theme.palette.mode === 'light' ? theme.palette.grey[400] : theme.palette.grey[600],
    fontSize: '3em',
  },
  [`& .${classes.button}`]: {
    margin: '10px 10px 0px 0px',  // top, right, bottom, left
  },
}));

export interface UpdateUserDialogProps {
  open: boolean;
  setOpen?: (open: boolean) => void;
  user: User,
  updateEmail: (email: string) => Promise<void> | void;
  updatePassword: (updatePasswordDto: UpdatePasswordDto) => Promise<void> | void;
}

export interface UserProfileCardProps {
  user: User;
  updateEmail: (email: string) => void;
  updatePassword: (updatePasswordDto: UpdatePasswordDto) => void;
}

export function UserProfileCard({ user, updateEmail, updatePassword }: UserProfileCardProps): JSX.Element {
  const [openEmailUpdateDialog, setOpenEmailUpdateDialog] = React.useState(false);
  const [openPasswordUpdateDialog, setOpenPasswordUpdateDialog] = React.useState(false);

  return (
    <>
      <StyledCard variant="outlined">
        <CardHeader
          title={user.username}
          titleTypographyProps={{ variant: 'h5' }}
          subheader={user.is_admin ? 'Admin' : 'User'}
          avatar={<AccountIcon className={classes.avatar} />}
          action={
            <Button variant="contained" className={classes.button} onClick={() => {
              setOpenEmailUpdateDialog(false);
              setOpenPasswordUpdateDialog(true);
            }}>
              비밀번호 변경
            </Button>
          }
        />
        <CardContent>
          <Grid container spacing={2}>
            <Grid item xs={1}>
              <Typography style={{ textAlign: 'right' }}>Email :</Typography>
            </Grid>
            <Grid item xs={3}>
              <Typography>{user.email ? user.email : 'jeongwpa@stduent.42seoul.kr'}</Typography>
            </Grid>
            <Grid item xs={4}>
              <Button variant="contained" onClick={() => {
                setOpenPasswordUpdateDialog(false);
                setOpenEmailUpdateDialog(true);
              }}>
                이메일 변경
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </StyledCard>
      {openEmailUpdateDialog && (
        <UpdateEmailDialog
          open={openEmailUpdateDialog}
          setOpen={setOpenEmailUpdateDialog}
          user={user}
          updateEmail={updateEmail}
        />
      )}
      {openPasswordUpdateDialog && (
        <UpdatePasswordDialog
          open={openPasswordUpdateDialog}
          setOpen={setOpenPasswordUpdateDialog}
          user={user}
          updatePassword={updatePassword}
        />
      )}
    </>
  );
}
