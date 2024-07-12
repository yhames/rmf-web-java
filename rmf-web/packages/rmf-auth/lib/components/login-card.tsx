import { Button, styled, TextField, Typography } from '@mui/material';
import React, { useEffect } from 'react';
import Authenticator from '../authenticator';
import { JwtAuthenticator } from '../jwt';
import { useNavigate } from 'react-router-dom';

const prefix = 'login-card';
const classes = {
  container: `${prefix}-container`,
  title: `${prefix}-title`,
  logo: `${prefix}-logo`,
  input: `${prefix}-input`,
  button: `${prefix}-button`,
  errorMessage: `${prefix}-errorMessage`,
};
const StyledDiv = styled('div')(({ theme }) => ({
  [`&.${classes.container}`]: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    borderStyle: 'none',
    borderRadius: 20,
    borderColor: 'black',
    padding: '70px',
    width: 'fit-content',
    minWidth: 250,
    backgroundColor: 'snow',
    boxShadow: theme.shadows[12],
  },
  [`& .${classes.title}`]: {
    color: '#44497a',
  },
  [`& .${classes.logo}`]: {
    width: 100,
    margin: '25px 0px 50px 0px',
  },
  [`& .${classes.input}`]: {
    margin: '10px 0px 10px 0px',
  },
  [`& .${classes.button}`]: {
    margin: '40px 0px 0px 0px',
  },
  [`& .${classes.errorMessage}`]: {
    color: '#B91C1C',
  },
}));

export const LoginCard = React.forwardRef(
  (
    { title, logo, authenticator, onLoginClick, children }: LoginCardProps,
    ref: React.Ref<HTMLDivElement>,
  ): JSX.Element => {
    const [username, setUsername] = React.useState('');
    const [password, setPassword] = React.useState('');
    const [error, setError] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState('');
    const navigate = useNavigate();

    useEffect(() => {
      const redirected = new URLSearchParams(window.location.search).get('redirected')
      if (redirected) {
        navigate('/login');
        alert("장시간 미사용으로 로그아웃 되었습니다. 다시 로그인해주세요.")
      }
    }, [navigate]);

    useEffect(() => {
      if (authenticator && authenticator instanceof JwtAuthenticator) {
        authenticator.onChangeUser(username, password);
      }
    }, [authenticator, username, password]);

    const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.id === 'username') {
        setUsername(e.target.value);
      } else if (e.target.id === 'password') {
        setPassword(e.target.value);
      }
    };

    const onClickLogin = async (e: React.MouseEvent) => {
      if (!(authenticator instanceof JwtAuthenticator)) {
        onLoginClick && onLoginClick(e);
        return;
      }
      try {
        authenticator && await authenticator.loginWithPassword();
        setError(false);
        setErrorMessage('');
      } catch (err) {
        setError(true);
        setErrorMessage('Invalid username or password');
      }
    };

    return (
      <StyledDiv ref={ref} className={classes.container}>
        <Typography variant="h4" className={classes.title}>
          {title}
        </Typography>
        <img src={logo} alt="" className={classes.logo} />
        <TextField
          variant="outlined"
          required
          fullWidth
          name="username"
          label="Username"
          id="username"
          onChange={onChange}
          autoComplete="current-user"
          className={classes.input}
          error={error}
        />
        <TextField
          variant="outlined"
          required
          fullWidth
          name="password"
          label="Password"
          type="password"
          id="password"
          onChange={onChange}
          autoComplete="current-password"
          className={classes.input}
          error={error}
        />
        <Typography variant="caption" className={classes.errorMessage}>
          {errorMessage}
        </Typography>
        <Button variant="contained" aria-label="Login" onClick={onClickLogin} className={classes.button}>
          Login
        </Button>
        {children}
      </StyledDiv>
    );
  },
);

export interface LoginCardProps extends React.PropsWithChildren<{}> {
  title: string,
  logo: string,
  authenticator?: Authenticator,
  onLoginClick?: React.MouseEventHandler,
}

export default LoginCard;
