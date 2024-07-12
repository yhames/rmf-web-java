import { Checkbox, FormControlLabel, TextField } from '@mui/material';
import React from 'react';
import { ConfirmationDialog, useAsync } from 'react-components';
import { AppControllerContext } from '../app-contexts';
import { User } from 'api-client';

export interface CreateUserDialogProps {
  open: boolean;
  setOpen?: (open: boolean) => void;
  createUser?: (user: User) => Promise<void> | void;
}

export function CreateUserDialog({
  open,
  setOpen,
  createUser,
}: CreateUserDialogProps): JSX.Element {
  const safeAsync = useAsync();
  const [creating, setCreating] = React.useState(false);
  const [username, setUsername] = React.useState('');
  const [usernameError, setUsernameError] = React.useState(false);
  const [email, setEmail] = React.useState('');
  const [emailError, setEmailError] = React.useState(false);
  const [isAdmin, setIsAdmin] = React.useState(false);
  const { showAlert } = React.useContext(AppControllerContext);

  const validateForm = () => {
    let error = false;
    if (!username) {
      setUsernameError(true);
      error = true;
    } else {
      setUsernameError(false);
    }
    if (!email) {
      setEmailError(true);
      error = true;
    } else {
      setEmailError(false);
    }
    return !error;
  };

  const submitForm = async () => {
    if (!validateForm()) {
      return;
    }
    setCreating(true);
    try {
      createUser && (await safeAsync(createUser({ username, email, is_admin: isAdmin } as User)));
      setCreating(false);
      setOpen && setOpen(false);
    } catch (e) {
      setCreating(false);
      showAlert('error', `Failed to create user: ${(e as Error).message}`);
    }
  };

  return (
    <ConfirmationDialog
      open={open}
      title="Create User"
      confirmText="Create"
      submitting={creating}
      onSubmit={submitForm}
      onClose={() => setOpen && setOpen(false)}
    >
      <TextField
        id="username"
        variant="outlined"
        fullWidth
        autoFocus
        margin="normal"
        label="Username"
        value={username}
        onChange={(ev) => setUsername(ev.target.value)}
        error={usernameError}
        helperText="Required"
      />
      <TextField
        id="email"
        variant="outlined"
        fullWidth
        margin="normal"
        label="email"
        type="email"
        value={email}
        onChange={(ev) => setEmail(ev.target.value)}
        error={emailError}
        helperText="Required"
      />
      <FormControlLabel
        style={{margin: '0px 0px 0px 10px'}}
        control={<Checkbox onChange={ev => setIsAdmin(ev.target.checked)} />}
        label="Admin"
      />
    </ConfirmationDialog>
  );
}
