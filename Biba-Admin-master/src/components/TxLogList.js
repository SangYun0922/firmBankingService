import React from 'react'
import {
    List,
    Datagrid,
    TextField,
} from 'react-admin'

export default function TxLogList(props) {
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
                <TextField source="MsgId" />
                <TextField source="EncData" />
            </Datagrid>
        </List>
    )
}
