import { TextField, Grid, Button } from '@mui/material';
import React from 'react';
import { ConfirmationDialog, useAsync } from 'react-components';
import { UpdateUserDialogProps } from './user-profile';
import { AppControllerContext } from '../app-contexts';

/**
 * @TODO: 이메일 인증코드 발송 API 호출, 인증코드 유효시간 설정, 인증코드 재전송 기능 추가
 */
export function UpdateEmailDialog(
  { open, setOpen, user, updateEmail }: Omit<UpdateUserDialogProps, 'updatePassword'>)
  : JSX.Element {
  const safeAsync = useAsync();
  const { showAlert } = React.useContext(AppControllerContext);

  const [newEmail, setNewEmail] = React.useState('');
  const [code, setCode] = React.useState('');

  const [newEmailError, setNewEmailError] = React.useState(false);
  const [codeError, setCodeError] = React.useState(false);

  const [newEmailMessage, setNewEmailMessage] = React.useState('');
  const [codeMessage, setCodeMessage] = React.useState('');

  const [isSentEmail, setIsSentEmail] = React.useState(false);
  const [updating, setUpdating] = React.useState(false);

  const validateForm = () => {
    let error = false;
    if (!isSentEmail) {
      error = true;
      showAlert('error', 'Please send verification email first');
    }
    if (isEmptyEmail() || isEmptyCode()) {
      error = true;
    }
    return !error;
  };

  const isEmptyEmail = () => {
    if (!newEmail) {
      setNewEmailError(true);
      setNewEmailMessage('Required');
      return true;
    }
    setNewEmailError(false);
    setNewEmailMessage('');
    return false;
  };

  const isEmptyCode = () => {
    if (!code) {
      setCodeError(true);
      setCodeMessage('Required');
      return true;
    }
    setCodeError(false);
    setCodeMessage('');
    return false;
  };

  const sendEmail = async () => {
    if (isEmptyEmail()) {
      return;
    }
    setIsSentEmail(true);
  };

  const submitForm = async () => {
    if (!validateForm()) {
      return;
    }
    setUpdating(true);
    try {
      updateEmail && (await safeAsync(updateEmail(newEmail)));
      setUpdating(false);
      setOpen && setOpen(false);
    } catch (err) {
      setUpdating(false);
      showAlert('error', `Failed to update email: ${(err as Error).message}`);
    }
  };

  return (
    <>
      <ConfirmationDialog
        open={open}
        title="Update Email"
        confirmText="Submit"
        submitting={updating}
        onSubmit={submitForm}
        onClose={() => setOpen && setOpen(false)}
      >
        <Grid container alignItems="center" spacing={1}>
          <Grid item>
            <TextField
              id="email"
              variant="outlined"
              fullWidth
              autoFocus
              margin="normal"
              label="Email"
              type="email"
              value={newEmail}
              onChange={(ev) => setNewEmail(ev.target.value)}
              error={newEmailError}
              helperText={newEmailMessage}
            />
          </Grid>
          <Grid item>
            <Button variant="contained" onClick={sendEmail}>
              Send
            </Button>
          </Grid>
        </Grid>
        <Grid container alignItems="center" spacing={1}>
          <Grid item>
            <TextField
              id="verification"
              variant="outlined"
              margin="normal"
              label="Verification"
              type="verification"
              value={code}
              onChange={(ev) => setCode(ev.target.value)}
              error={codeError}
              helperText={codeMessage}
            />
          </Grid>
        </Grid>
      </ConfirmationDialog>
    </>
  );
}
