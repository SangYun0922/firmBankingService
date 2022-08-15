import React from 'react'
import {
    List,
    Create,
    Edit,
    Show,
    Filter,
    Datagrid,
    TextField,
    UrlField,
    EmailField,
    TextInput,
    SimpleForm,
    EditButton,
    SimpleShowLayout,
    ShowButton
} from 'react-admin'

export const CustFilter = (props) => (
    <Filter {...props}>
        <TextInput label="Search" source="q" alwaysOn />
    </Filter>
);

export const CustShow = (props) => (
    <Show title="Customer View" {...props}>
        <SimpleShowLayout>
            <TextField source="id" />
            <TextField source="CustNm" />
            <TextField source="OrgCd" />
            <UrlField source="CallbackURL" />
            <TextField source="ApiKey" />
            <TextField source="PriContactNm" />
            <TextField source="PriContactTel" />
            <EmailField source="PriContactEmail" />
            <TextField source="SecContactNm" />
            <TextField source="SecContactTel" />
            <EmailField source="SecContactEmail" />
            <TextField source="TxSequence" />
            <TextField source="InUse" />
            <TextField source="CreatedAt" />
            <TextField source="UpdatedAt" />
        </SimpleShowLayout>
    </Show>
);

export const CustList = (props) => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" />
            <TextField source="CustNm" />
            <TextField source="OrgCd" />
            <UrlField source="CallbackURL" />
            <TextField source="ApiKey" />
            <TextField source="TxSequence" />
            <TextField source="InUse" />
            <ShowButton />
        </Datagrid>
    </List>
)

export const CustCreate = (props) => (
    <Create {...props}>
        <SimpleForm>
            <TextInput source="CustId" />
            <TextInput source="CustNm" />
            <TextInput source="OrgCd" />
            <TextInput source="CallbackURL" />
            <TextInput source="ApiKey" />
            <TextInput source="PriContactNm" />
            <TextInput source="PriContactTel" />
            <TextInput source="PriContactEmail" />
            <TextInput source="SecContactNm" />
            <TextInput source="SecContactTel" />
            <TextInput source="SecContactEmail" />
            <TextInput source="TxSequence" />
            <TextInput source="InUse" />
        </SimpleForm>
    </Create>
);

export const CustEdit = (props) => (
    <Edit title="Edit Cust" {...props}>
        <SimpleForm>
            <TextInput source="id" />
            <TextInput source="CustNm" />
            <TextInput source="OrgCd" />
            <TextInput source="CallbackURL" />
            <TextInput source="ApiKey" />
            <TextInput source="PriContactNm" />
            <TextInput source="PriContactTel" />
            <TextInput source="PriContactEmail" />
            <TextInput source="SecContactNm" />
            <TextInput source="SecContactTel" />
            <TextInput source="SecContactEmail" />
            <TextInput source="TxSequence" />
            <TextInput source="InUse" />
        </SimpleForm>
    </Edit>
);