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
                <TextField source="TxDate" />
                <TextField source="CustId" />
                <TextField source="CustNm" />
                <TextField source="OrgCd" />
                <TextField source="TxSequence" />
                <TextField source="TxStarted" />
            </Datagrid>
        </List>
    )
}
