import React from 'react'
import {
    List,
    Create,
    Datagrid,
    TextField,
    EmailField,
    UrlField,
    TextInput,
    SimpleForm,
} from 'react-admin'

export const CustList = (props) => (
    <List {...props}>
        <Datagrid>
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