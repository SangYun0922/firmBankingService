import React from 'react'
import {
    List,
    Datagrid,
    TextField,

    EmailField,
    UrlField
} from 'react-admin'

export default function CustList(props) {
    return (
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
}
