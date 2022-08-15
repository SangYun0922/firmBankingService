import React from 'react'
import {
    List,
    Datagrid,
    TextField,
    Show,
    SimpleShowLayout,
    ShowButton,
} from 'react-admin'

export const TxStatList = (props) => (
    <List {...props}>
        <Datagrid>
            <TextField source="TxDate" />
            <TextField source="OrgCd" />
            <TextField source="CustNm" />
            <TextField source="BankNm" />
            <TextField source="TxType" />
            <TextField source="TxCnt" />
            <TextField source="TxSize" />
        </Datagrid>
    </List>
);
