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
                <TextField source="CustId" />
                <TextField source="TxDate" />
                <TextField source="TelegramNo" />
                <TextField source="TxType" />
                <TextField source="BankCd" />
                <TextField source="Size" />
                <TextField source="RoundTrip" />
                <TextField source="StmtCnt" />
                <TextField source="Status" />
                <TextField source="StartDT" />
                <TextField source="EndDT" />
                <TextField source="NatvTrNo" />
                <TextField source="ErrCode" />
                <TextField source="ErrMsg" />
                <TextField source="EncData" />
            </Datagrid>
        </List>
    )
}
