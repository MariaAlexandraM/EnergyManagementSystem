import React from 'react';
import APIResponseErrorMessage from "../commons/errorhandling/api-response-error-message";
import {
    Button,
    Card,
    CardHeader,
    Col,
    Modal,
    ModalBody,
    ModalHeader,
    Row
} from 'reactstrap';
import DeviceForm from "./components/device-form";
import * as API_USERS from "./api/device-api";
import DeviceTable from "./components/device-table";
import { HOST } from '../commons/hosts';
import RestApiClient from "../commons/api/rest-client";


class DeviceContainer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedDevice: null,  
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null,
            showForm: false, 
        };
    }

    componentDidMount() {
        this.fetchDevices();
    }

    // fetchDevices() {
    //     return API_USERS.getDevices((result, status, err) => {
    //         if (result !== null && status === 200) {
    //             this.setState({
    //                 tableData: result,
    //                 isLoaded: true
    //             });
    //         } else {
    //             this.setState({
    //                 errorStatus: status,
    //                 error: err
    //             });
    //         }
    //     });
    // }

    fetchDevices() {
        const userRole = localStorage.getItem('userRole');
        const userId = localStorage.getItem('userId');
    
        let url = userRole === 'admin' 
            ? HOST.device_backend_api + "/device" 
            : `${HOST.device_backend_api}/device/user/${userId}/deviceList`;
    
        let request = new Request(url, { method: 'GET' });
    
        console.log("Fetching devices from:", request.url);
    
        RestApiClient.performRequest(request, (result, status, err) => {
            if (result !== null && status === 200) {
                this.setState({
                    tableData: result,
                    isLoaded: true
                });
            } else {
                this.setState({
                    errorStatus: status,
                    error: err
                });
            }
        });
    }
    

    handleRowClick = () => {
    };

    toggleForm = () => {
        this.setState({ 
            showForm: !this.state.showForm,
            selectedDevice: null  
        });
    };

    reload = () => {
        this.setState({ isLoaded: false });
        this.fetchDevices(); 
    };

    handleEditDevice = (device) => {
        this.setState({ selectedDevice: device, showForm: true }); 
    };
    

    render() {
        return (
            <div>
                <CardHeader>
                    <strong>Device Management</strong>
                </CardHeader>
                <Card>
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button color="primary" onClick={this.toggleForm}>
                                {this.state.showForm ? "Cancel" : "Add Device"}
                            </Button>
                        </Col>
                    </Row>

                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            {this.state.isLoaded && (
                                <DeviceTable
                                    tableData={this.state.tableData}
                                    onRowClick={this.handleRowClick}  
                                    reload={this.reload}
                                    onEditDevice={this.handleEditDevice}
                                />
                            )}
                            {this.state.errorStatus > 0 && (
                                <APIResponseErrorMessage
                                    errorStatus={this.state.errorStatus}
                                    error={this.state.error}
                                />
                            )}
                        </Col>
                    </Row>

                    {/* deschide formu */}
                    <Modal isOpen={this.state.showForm} toggle={this.toggleForm} size="lg">
                        <ModalHeader toggle={this.toggleForm}>
                            {this.state.selectedDevice ? "Edit Device" : "Add Device"}
                        </ModalHeader>
                        <ModalBody>
                            <DeviceForm
                                selectedDevice={this.state.selectedDevice}
                                reloadHandler={this.reload} 
                                toggleForm={this.toggleForm}  
                            />
                        </ModalBody>
                    </Modal>
                </Card>
            </div>
        );
    }
}

export default DeviceContainer;
