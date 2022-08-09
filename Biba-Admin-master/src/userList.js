import * as React from "react";
import {
  List,
  Datagrid,
  EmailField,
  Filter,
  DateField,
  SearchInput,
  Pagination,
  EditButton,
  DeleteButton,
  ImageField,
  Create,
  SimpleForm,
  TextInput,
  Show,
  SimpleShowLayout,
  TextField,
  RichTextField,
  Edit,
} from "react-admin";
import MyUrlField from "./MyUrlField";

const UserPagination = (props) => (
  <Pagination rowsPerPageOptions={[5, 10]} {...props} />
);

const UserFilter = (props) => (
  <Filter {...props}>
    <TextInput label="Search" source="q" alwaysOn />
  </Filter>
);

export const UserList = (props) => (
  <List {...props} filters={<UserFilter />}>
    <Datagrid rowClick="edit">
      <TextField source="id" />
      <TextField source="email" />
      <TextField source="nickname" />
      <ImageField source="profile" />
      <DateField source="createdAt" />
      <DateField source="updatedAt" />
      <EditButton basePath="/posts" />
      <DeleteButton />
    </Datagrid>
  </List>
);

export const UserCreate = (props) => (
  <Create {...props}>
    <SimpleForm>
      <TextInput source="email" />
      <TextInput source="password" />
      <TextInput source="nickname" />
      <TextInput source="profile" />
    </SimpleForm>
  </Create>
);

export const UserShow = (props) => (
  <Show title="User view" {...props}>
    <SimpleShowLayout>
      <TextField source="title" />
      <TextField source="email" />
      <TextField source="password" />
      <TextField source="nickname" />
      <RichTextField source="profile" />
    </SimpleShowLayout>
  </Show>
);

export const UserEdit = (props) => (
  <Edit {...props}>
    <SimpleForm>
      <TextInput source="id" />
      <TextInput source="email" />
      <TextInput source="nickname" />
      <TextInput source="profile" />
    </SimpleForm>
  </Edit>
);
