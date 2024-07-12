import { TextField } from '@mui/material';
import React from 'react';
import { ConfirmationDialog, useAsync } from 'react-components';
import { UpdateUserDialogProps } from './user-profile';
import { AppControllerContext } from '../app-contexts';
import { UpdatePasswordDto } from './update-user-api';

export function UpdatePasswordDialog(
  { open, setOpen, user, updatePassword }: Omit<UpdateUserDialogProps, 'updateEmail'>)
  : JSX.Element {
  const currentPasswordId: string = 'current password';
  const newPasswordId: string = 'new password';
  const confirmPasswordId: string = 'confirm password';

  const safeAsync = useAsync();
  const { showAlert } = React.useContext(AppControllerContext);

  const [updatePasswordDto, setUpdatePasswordDto] = React.useState<UpdatePasswordDto>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const [currentPasswordError, setCurrentPasswordError] = React.useState(false);
  const [newPasswordError, setNewPasswordError] = React.useState(false);
  const [confirmPasswordError, setConfirmPasswordError] = React.useState(false);

  const [currentPasswordMessage, setCurrentPasswordMessage] = React.useState('');
  const [newPasswordMessage, setNewPasswordMessage] = React.useState('');
  const [confirmPasswordMessage, setConfirmPasswordMessage] = React.useState('');

  const [updating, setUpdating] = React.useState(false);

  const validateForm = () => {
    let error = false;
    if (currentPasswordIsEmpty()) {
      error = true;
    }
    if (newPasswordIsSameWithCurrentPassword()) {
      error = true;
    }
    if (confirmPasswordIsNotSameWithNewPassword()) {
      error = true;
    }
    return !error;
  };

  const currentPasswordIsEmpty = () => {
    let empty = false;
    if (!updatePasswordDto.currentPassword) {
      setCurrentPasswordError(true);
      setCurrentPasswordMessage('Required');
      empty = true;
    } else {
      setCurrentPasswordError(false);
      setCurrentPasswordMessage('');
    }
    return empty;
  };

  const newPasswordIsSameWithCurrentPassword = () => {
    if (!updatePasswordDto.newPassword) {
      setNewPasswordError(true);
      setNewPasswordMessage('Required');
      return true;
    }
    if (updatePasswordDto.currentPassword === updatePasswordDto.newPassword) {
      setNewPasswordError(true);
      setNewPasswordMessage('New password must be different from current password');
      return true;
    }
    setNewPasswordError(false);
    setNewPasswordMessage('');
    return false;
  };

  const confirmPasswordIsNotSameWithNewPassword = () => {
    if (!updatePasswordDto.confirmPassword) {
      setConfirmPasswordError(true);
      setConfirmPasswordMessage('Required');
      return true;
    }
    if (updatePasswordDto.newPassword !== updatePasswordDto.confirmPassword) {
      setConfirmPasswordError(true);
      setConfirmPasswordMessage('Passwords do not match');
      return true;
    }
    setConfirmPasswordError(false);
    setConfirmPasswordMessage('');
    return false;
  };

  const submitForm = async () => {
    if (!validateForm()) {
      return;
    }
    setUpdating(true);
    try {
      updatePassword && await safeAsync((updatePassword(updatePasswordDto)));
      setUpdating(false);
      setOpen && setOpen(false);
      showAlert('success', 'Password updated successfully');
    } catch (err) {
      setCurrentPasswordError(true);
      setUpdating(false);
      showAlert('error', `Failed to update password: ${(err as Error).message}`);
    }
  };

  const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.id === currentPasswordId) {
      setUpdatePasswordDto((prev) => ({ ...prev, currentPassword: e.target.value }));
    }
    if (e.target.id === newPasswordId) {
      setUpdatePasswordDto((prev) => ({ ...prev, newPassword: e.target.value }));
    }
    if (e.target.id === confirmPasswordId) {
      setUpdatePasswordDto((prev) => ({ ...prev, confirmPassword: e.target.value }));
    }
  }

  return (
    <>
      <ConfirmationDialog
        open={open}
        title="Update Password"
        confirmText="Submit"
        submitting={updating}
        onSubmit={submitForm}
        onClose={() => setOpen && setOpen(false)}
      >
        <TextField
          id={currentPasswordId}
          variant="outlined"
          fullWidth
          autoFocus
          margin="normal"
          label="Current Password"
          type="password"
          value={updatePasswordDto.currentPassword}
          onChange={onChange}
          error={currentPasswordError}
          helperText={currentPasswordMessage}
        />
        <TextField
          id={newPasswordId}
          variant="outlined"
          fullWidth
          margin="normal"
          label="New Password"
          type="password"
          value={updatePasswordDto.newPassword}
          onChange={onChange}
          error={newPasswordError}
          helperText={newPasswordMessage}
        />
        <TextField
          id={confirmPasswordId}
          variant="outlined"
          fullWidth
          margin="normal"
          label="Confirm Password"
          type="password"
          value={updatePasswordDto.confirmPassword}
          onChange={onChange}
          error={confirmPasswordError}
          helperText={confirmPasswordMessage}
        />
      </ConfirmationDialog>
    </>
  );
}
