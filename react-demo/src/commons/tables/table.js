import React, {Component} from "react";
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import Field from "./fields/Field";
import {Col, Row} from "react-bootstrap";
class Table extends Component {
    constructor(props) {
        super(props);

        this.state = {
            data: props.data,
            columns: props.columns,
            search: props.search,
            filters: [],
            getTrPropsFunction: props.getTrProps,
            pageSize: props.pageSize || 10,
        };
    }

    filter(data) {
        let accepted = true;
        this.state.filters.forEach(val => {
            if (String(val.value).trim() === "") {
                accepted = true;
            } else if (!String(data[val.accessor]).includes(String(val.value))) {
                accepted = false;
            }
        });

        return accepted;
    }

    handleChange(value, index, header) {
        const filters = [...this.state.filters];  
        filters[index] = {
            value: value.target.value,
            accessor: header
        };
    
        this.setState({ filters });  
    }
    getTRPropsType = (state, rowInfo) => {
        const { getTrPropsFunction } = this.state;

        if (rowInfo) {
            return {
                style: {
                    textAlign: "center",
                    cursor: "pointer",
                },
                onClick: () => {
                    const rowData = rowInfo.original;  
                    console.log("Row clicked. Data: ", rowData);

                    if (getTrPropsFunction) {
                        getTrPropsFunction(rowData); 
                    }
                },
            };
        }
        return {};
    };

    render() {
        let data = this.state.data ? this.state.data.filter(data => this.filter(data)) : [];

        return (
            <div>
                <Row>
                    {this.state.search.map((header, index) => {
                        return (
                            <Col key={index}>
                                <div>
                                    <Field
                                        id={header.accessor}
                                        label={header.accessor}
                                        onChange={(e) => this.handleChange(e, index, header.accessor)}
                                    />
                                </div>
                            </Col>
                        );
                    })}
                </Row>
                <Row>
                    <Col>
                        <ReactTable
                            data={data}
                            resolveData={data => data.map(row => row)}
                            columns={this.state.columns}
                            defaultPageSize={this.state.pageSize}
                            getTrProps={this.getTRPropsType} // Pass the updated function
                            showPagination={true}
                            style={{
                                height: '300px',
                            }}
                        />
                    </Col>
                </Row>
            </div>
        );
    }
}

export default Table;
