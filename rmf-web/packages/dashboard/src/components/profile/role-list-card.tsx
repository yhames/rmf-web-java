import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Card,
  CardHeader,
  CardProps,
  Divider,
  Grid,
  styled,
  Typography,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import SecurityIcon from '@mui/icons-material/Security';
import { Permission } from 'api-client';
import React from 'react';
import { Loading, useAsync } from 'react-components';
import { AppControllerContext } from '../app-contexts';
import { PermissionsCard, PermissionsCardProps } from './permissions-card';

const prefix = 'role-list-card';
const classes = {
  permissionsCard: `${prefix}-permissionscard`,
  deleteRoleButton: `${prefix}-deleterolebutton`,
};
const StyledCard = styled((props: CardProps) => <Card {...props} />)(() => ({
  [`& .${classes.permissionsCard}`]: {
    width: '100%',
  },
  [`& .${classes.deleteRoleButton}`]: {
    float: 'right',
  },
}));

interface RoleAccordionProps extends Pick<PermissionsCardProps, 'getPermissions'> {
  role: string;
}

function RoleAccordion({
  role,
  getPermissions,
}: RoleAccordionProps) {
  return (
    <Accordion TransitionProps={{ unmountOnExit: true }} defaultExpanded={true} >
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography>{role}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid container direction="column" wrap="nowrap">
          <Grid item>
            <PermissionsCard
              className={classes.permissionsCard}
              getPermissions={getPermissions}
            />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}

export interface RoleListCardProps {
  getRoles?: () => Promise<string[]> | string[];
  deleteRole?: (role: string) => Promise<void> | void;
  getPermissions?: (role: string) => Promise<Permission[]> | Permission[];
  savePermission?: (role: string, permission: Permission) => Promise<void> | void;
  removePermission?: (role: string, permission: Permission) => Promise<void> | void;
  createRole?: (role: string) => Promise<void> | void;
}

export function RoleListCard({ getRoles, getPermissions, }: RoleListCardProps): JSX.Element {
  const safeAsync = useAsync();
  const [roles, setRoles] = React.useState<string[]>([]);
  const [loading, setLoading] = React.useState(true);
  const { showAlert } = React.useContext(AppControllerContext);

  const refresh = React.useCallback(async () => {
    if (!getRoles) return;
    setLoading(true);
    try {
      const newRoles = await safeAsync(getRoles());
      setRoles(newRoles.sort());
    } catch (e) {
      showAlert('error', `Failed to get roles: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, [getRoles, showAlert, safeAsync]);

  React.useEffect(() => {
    refresh();
  }, [refresh]);

  const getRolePermissions = React.useMemo(
    () => roles.map((r) => getPermissions && (() => getPermissions(r))),
    [roles, getPermissions],
  );

  return (
    <StyledCard variant="outlined">
      <CardHeader
        title="Roles"
        titleTypographyProps={{ variant: 'h5' }}
        avatar={<SecurityIcon />}
      />
      <Divider />
      <Loading loading={loading} size="50px">
        {roles.map((r, i) => (
          <RoleAccordion
            key={r}
            role={r}
            getPermissions={getRolePermissions[i]}
          />
        ))}
        {roles.length === 0 && <div style={{ height: 100 }} />}
      </Loading>
    </StyledCard>
  );
}
