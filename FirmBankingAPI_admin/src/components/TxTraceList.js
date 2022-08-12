import React from 'react'
import {
    List,
    Datagrid,
    TextField,
} from 'react-admin'

export default function TxTraceList(props) {
    return (
        <List {...props}>
            <Datagrid>
                <TextField source="CustId" />
                <TextField source="OrgCd" />
                <TextField source="TxDate" />
                <TextField source="TxSequence" />
                <TextField source="TxStarted" />
            </Datagrid>
        </List>
    )
}
