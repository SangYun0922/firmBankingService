import React from 'react'
import {
    List,
    Datagrid,
    TextField,
} from 'react-admin'

export default function TxStatList(props) {
    return (
        <List {...props}>
            <Datagrid>
                <TextField source="CustId" />
                <TextField source="TxDate" />
                <TextField source="BankCd" />
                <TextField source="TxType" />
                <TextField source="TxCnt" />
                <TextField source="TxSize" />
            </Datagrid>
        </List>
    )
}
