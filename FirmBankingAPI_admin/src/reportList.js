import * as React from "react";
import {
  Datagrid,
  TextField,
  ReferenceField,
  NumberField,
  DateField,
  List,
  DeleteButton,
} from "react-admin";

export const ReportList = (props) => (
  <List {...props}>
    <Datagrid rowClick="edit">
      <TextField source="id" />
      {/* <ReferenceField source="user_id" reference="users"><TextField source="id" /></ReferenceField> */}
      <TextField source="beer_name" />
      <TextField source="comment" />
      <NumberField source="recommend" />
      <NumberField source="request" />
      <DateField source="createdAt" />
      <DateField source="updatedAt" />
      <DeleteButton />
    </Datagrid>
  </List>
);
