import React from 'react'
import {
    List,
    Edit,
    Filter,
    Datagrid,
    TextField,
    Create,
    SimpleForm,
    TextInput,
    EditButton,
    Show,
    SimpleShowLayout,
    ShowButton,
} from 'react-admin'

export const BankFilter = (props) => (
    <Filter {...props}>
        <TextInput label="Search" source="q" alwaysOn />
    </Filter>
);

export const BankList = (props) => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" />
            <TextField source="BankCd" />
            <TextField source="BankNm" />
            <TextField source="SwiftCd" />
            <ShowButton />
        </Datagrid>
    </List>
);

export const BankCreate = (props) => (
    <Create {...props}>
        <SimpleForm>
            <TextInput source="BankId" />
            <TextInput source="BankCd" />
            <TextInput source="BankNm" />
            <TextInput source="SwiftCd" />
        </SimpleForm>
    </Create>
);

export const BankShow = (props) => (
    <Show title="Bank View" {...props}>
        <SimpleShowLayout>
            <TextField source="id" />
            <TextField source="BankCd" />
            <TextField source="BankNm" />
            <TextField source="SwiftCd" />
            <TextField source="CreatedAt" />
            <TextField source="UpdatedAt" />
        </SimpleShowLayout>
    </Show>
);

export const BankEdit = (props) => (
    <Edit title="Edit Bank" {...props}>
        <SimpleForm>
            <TextInput source="id" />
            <TextInput source="BankCd" />
            <TextInput source="BankNm" />
            <TextInput source="SwiftCd" />
        </SimpleForm>
    </Edit>
);