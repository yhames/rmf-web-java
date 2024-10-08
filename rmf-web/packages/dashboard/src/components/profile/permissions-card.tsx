import {
  Paper,
  PaperProps,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Toolbar,
  Typography,
} from '@mui/material';
import { Permission } from 'api-client';
import React from 'react';
import { styled } from '@mui/material';
import { Loading, useAsync } from 'react-components';
import { AppControllerContext } from '../app-contexts';
import { getActionText } from '../permissions';

const prefix = 'permissions-card';
const classes = {
  title: `${prefix}-title`,
  tableContainer: `${prefix}-table-container`,
  controlsButton: `${prefix}-controls-button`,
};
const StyledPaper = styled((props: PaperProps) => <Paper {...props} />)(({ theme }) => ({
  [`& .${classes.title}`]: {
    flex: '1 1 100%',
  },
  [`& .${classes.tableContainer}`]: {
    marginLeft: theme.spacing(4),
    marginRight: theme.spacing(4),
    width: 'auto',
  },
  [`& .${classes.controlsButton}`]: {
    float: 'right',
  },
}));

export interface PermissionsCardProps extends PaperProps {
  getPermissions?: () => Promise<Permission[]> | Permission[];
}

export function PermissionsCard({
  getPermissions,
  ...otherProps
}: PermissionsCardProps): JSX.Element {
  const safeAsync = useAsync();
  const [loading, setLoading] = React.useState(false);
  const [permissions, setPermissions] = React.useState<Permission[]>([]);
  const { showAlert } = React.useContext(AppControllerContext);

  const refresh = React.useCallback(async () => {
    if (!getPermissions) return;
    setLoading(true);
    try {
      const newPermissions = await safeAsync(getPermissions());
      // sort by action first, then by authorization group
      newPermissions.sort((a, b) => {
        if (a.action < b.action) return -1;
        if (a.action > b.action) return 1;
        if (a.authz_grp < b.authz_grp) return -1;
        if (a.authz_grp > b.authz_grp) return 1;
        return 0;
      });
      setPermissions(newPermissions);
    } catch (e) {
      showAlert('error', `Failed to get permissions: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, [getPermissions, showAlert, safeAsync]);

  React.useEffect(() => {
    refresh();
  }, [refresh]);

  return (
    <StyledPaper elevation={0} {...otherProps}>
      <Toolbar>
        <Typography variant="h6" className={classes.title}>
          Permissions
        </Typography>
      </Toolbar>
      <Loading loading={loading}>
        <TableContainer id="permission-table" className={classes.tableContainer}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Action</TableCell>
                <TableCell>Authorization Group</TableCell>
                <TableCell></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {permissions.map((p, idx) => (
                <TableRow key={idx}>
                  <TableCell>{getActionText(p.action)}</TableCell>
                  <TableCell>{p.authz_grp}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Loading>
    </StyledPaper>
  );
}
