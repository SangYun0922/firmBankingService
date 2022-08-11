import * as React from "react";
import { Fragment } from "react";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";

import {
  List,
  Datagrid,
  TextField,
  ReferenceField,
  NumberField,
  DateField,
  Filter,
  TextInput,
  ReferenceInput,
  ReferenceArrayInput,
  SelectArrayInput,
  SelectInput,
  EditButton,
  BulkDeleteButton,
  Show,
  SimpleShowLayout,
  RichTextField,
  Edit,
  SimpleForm,
  NumberInput,
  DateInput,
  Create,
  ImageField,
  DeleteButton,
} from "react-admin";
import MyUrlField from "./MyUrlField";
import IconEvent from "@material-ui/icons/Event";

const BeerTitle = ({ record }) => {
  return <span>Beer {record ? `"${record.title}"` : ""}</span>;
};

const BeerFilter = (props) => (
  <Filter {...props}>
    <TextInput label="Search" source="q" alwaysOn />
  </Filter>
);

export const BeerlistShow = (props) => (
  <Show {...props}>
    <SimpleShowLayout>
      {/* <TextField source="beer_name" />
      <TextField source="beer_name_en" /> */}
      <TextField source="search_word" />
      {/* <TextField source="beer_img" /> */}
      {/* <NumberField source="abv" />
      <NumberField source="ibu" /> */}
      {/* <ReferenceField source="style_id" reference="styles">
        <TextField source="id" />
      </ReferenceField>
      <ReferenceField source="company_id" reference="companies">
        <TextField source="id" />
      </ReferenceField>
      <ReferenceField source="country_id" reference="countries">
        <TextField source="id" />
      </ReferenceField> */}
      {/* <NumberField source="rate" /> */}
      <TextField source="story" />
      <TextField source="explain" />
      {/* <TextField source="source" /> */}
      {/* <TextField source="poster" /> */}
      <ImageField source="poster" />
      <DateField source="createdAt" />
      <DateField source="updatedAt" />
    </SimpleShowLayout>
  </Show>
);

export const BeerList = (props) => (
  <List {...props} filters={<BeerFilter />}>
    <Datagrid rowClick="edit" expand={<BeerlistShow />}>
      <TextField source="id" />
      <TextField source="beer_name" />
      <TextField source="beer_name_en" />
      {/* <TextField source="search_word" /> */}
      <ImageField source="beer_img" />
      <NumberField source="abv" />
      <NumberField source="ibu" />
      <NumberField source="style_id" />
      <NumberField source="company_id" />
      <NumberField source="country_id" />
      <NumberField source="rate" />
      {/* <TextField source="story" /> */}
      {/* <TextField source="explain" /> */}
      {/* <MyUrlField source="source" /> */}
      {/* <ImageField source="poster" /> */}
      {/* <DateField source="createdAt" />
      <DateField source="updatedAt" /> */}
      <EditButton />
      {/* <DeleteButton /> */}
    </Datagrid>
  </List>
);

export const BeerCreate = (props) => (
  <Create {...props}>
    <SimpleForm>
      {/* <TextInput source="id" /> */}
      <TextInput source="beer_name" />
      <TextInput source="beer_name_en" />
      {/* <TextInput source="search_word" /> */}
      <TextInput source="beer_img" />
      {/* <NumberInput source="abv" />
      <NumberInput source="ibu" />
      <NumberInput source="style_id" />
      <NumberInput source="company_id" />
      <NumberInput source="country_id" /> */}
      <TextInput source="story" />
      <TextInput source="explain" />
      {/* <TextInput source="source" />
      <TextInput source="poster" /> */}
      <DateField source="createdAt" />
      <DateField source="updatedAt" />
    </SimpleForm>
  </Create>
);

export const BeerEdit = (props) => (
  <Edit {...props}>
    <SimpleForm>
      <TextInput source="id" />
      <TextInput source="beer_name" />
      <TextInput source="beer_name_en" />
      <TextInput source="search_word" />
      <TextInput source="beer_img" />
      <NumberInput source="abv" />
      <NumberInput source="ibu" />
      {/* <ReferenceArrayInput source="style_id" reference="styles">
        <SelectArrayInput optionText="id" />
      </ReferenceArrayInput>
      <ReferenceArrayInput source="company_id" reference="companies">
        <SelectInput optionText="id" />
      </ReferenceArrayInput>
      <ReferenceArrayInput source="country_id" reference="countries">
        <SelectInput optionText="id" />
      </ReferenceArrayInput> */}
      <NumberInput source="rate" />
      <TextInput source="story" />
      <TextInput source="explain" />
      <TextInput source="source" />
      <TextInput source="poster" />
      <DateInput source="createdAt" />
      <DateInput source="updatedAt" />
    </SimpleForm>
  </Edit>
);
