import React from 'react'
import {
    List,
    Datagrid,
    TextField,
    DateField,
    EditButton,
    DeleteButton,
    EmailField,
    UrlField
} from 'react-admin'

export default function BankList(props) {
    return (
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
    )
}
