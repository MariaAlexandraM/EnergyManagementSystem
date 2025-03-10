import React from "react";
import Table from "../../commons/tables/table";
import { Col, Row } from "reactstrap";
import Button from "react-bootstrap/Button";
import * as API_USERS from "../api/device-api";

const columns = [
    { Header: 'Description', accessor: 'description' },
    { Header: 'Address', accessor: 'address' },
    { Header: 'MaxHEC', accessor: 'maxHEC' },
    {
        Header: 'Assigned User',
        accessor: 'userId',
        Cell: ({ value }) => value ? `User ID: ${value}` : 'Not Assigned'
    }
];

const filters = [
    { accessor: 'description' },
    { accessor: 'address' },
    { accessor: 'maxHEC' },
    { accessor: 'user' }
];

class DeviceTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedDevice: null,
        };
    }

    handleRowClick = (device) => {
        console.log("Device clicked: ", device);
        if (this.props.onEditDevice) {
            this.props.onEditDevice(device);
        }
    };

    handleDelete = () => {
        const deviceId = this.state.selectedDevice?.id;
        if (!deviceId) return;

        if (window.confirm("Are you sure you want to delete this device?")) {
            API_USERS.deleteDevice(deviceId, (status, error) => {
                if (status === 200 || status === 204) {
                    console.log(`Device ${deviceId} deleted successfully`);
                    this.setState({ selectedDevice: null });
                    this.props.reload();
                } else {
                    console.error("[X] Error deleting device:", error);
                }
            });
        }
    };

    handleSave = () => {
        const updatedDevice = this.state.selectedDevice;
        console.log("📩 Saving device: ", updatedDevice);

        API_USERS.updateDevice(updatedDevice, (result, status, err) => {
            if (status === 200 || status === 201) {
                console.log(`Device ${updatedDevice.id} updated successfully`);
                this.setState({ selectedDevice: null });
                this.props.reload();
            } else {
                console.error("[X] Error updating device:", err);
            }
        });
    };

    render() {
        const { tableData = [] } = this.props;
        const { selectedDevice } = this.state;

        return (
            <div>
                <Table
                    data={tableData}
                    columns={columns}
                    search={filters}
                    pageSize={5}
                    getTrProps={this.handleRowClick}
                />

                {selectedDevice && (
                    <div>
                        <h3>Edit Device</h3>
                        <Row>
                            <Col sm="6">
                                <label>Description:</label>
                                <input
                                    type="text"
                                    value={selectedDevice.description}
                                    onChange={(e) => this.handleInputChange('description', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <label>Address:</label>
                                <input
                                    type="text"
                                    value={selectedDevice.address}
                                    onChange={(e) => this.handleInputChange('address', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <label>MaxHEC:</label>
                                <input
                                    type="number"
                                    value={selectedDevice.maxHEC}
                                    onChange={(e) => this.handleInputChange('maxHEC', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <Button onClick={this.handleSave} color="primary">Save</Button>
                                <Button onClick={this.handleDelete} color="danger" style={{ marginLeft: "10px" }}>Delete</Button>
                            </Col>
                        </Row>
                    </div>
                )}
            </div>
        );
    }

    handleInputChange = (field, value) => {
        this.setState({
            selectedDevice: {
                ...this.state.selectedDevice,
                [field]: value
            }
        });
    };
}

export default DeviceTable;
