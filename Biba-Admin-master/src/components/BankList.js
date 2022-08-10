import React from 'react'
import {
    List,
    Datagrid,
    TextField,
    Create,
    SimpleForm,
    TextInput,
} from 'react-admin'

export const BankList = (props) => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" />
            <TextField source="BankCd" />
            <TextField source="BankNm" />
            <TextField source="SwiftCd" />
            <TextField source="CreatedAt" />
            <TextField source="UpdatedAt" />
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