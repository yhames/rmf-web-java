import {
  Card,
  CardHeader,
  CardProps,
  Divider,
  List,
  ListItem,
  ListItemText,
  styled,
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import React from 'react';

const prefix = 'manage-roles-dialog';
const classes = {
  action: `${prefix}-action`,
  list: `${prefix}-list`,
};
const StyledCard = styled((props: CardProps) => <Card {...props} />)(({ theme }) => ({
  [`& .${classes.action}`]: {
    margin: 0,
  },
  [`& .${classes.list}`]: {
    paddingLeft: theme.spacing(1),
    paddingRight: theme.spacing(1),
  },
}));

export interface ManageRolesCardProps extends CardProps {
  assignedRoles: string[];
}

export function ManageRolesCard({
  assignedRoles,
  ...otherProps
}: ManageRolesCardProps): JSX.Element {

  return (
    <StyledCard variant="outlined" {...otherProps}>
      <CardHeader
        title="Roles"
        titleTypographyProps={{ variant: 'h5' }}
        avatar={<SecurityIcon />}
        classes={{ action: classes.action }}
      />
      <Divider />
      <List dense className={classes.list}>
        {assignedRoles.map((r) => (
          <ListItem key={r}>
            <ListItemText>{r}</ListItemText>
          </ListItem>
        ))}
      </List>
    </StyledCard>
  );
}
