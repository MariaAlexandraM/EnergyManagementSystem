import React from 'react';
import Button from "react-bootstrap/Button";
import * as API_USERS from "../api/device-api";
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import { Col, Row, FormGroup, Input, Label } from "reactstrap";
import { HOST } from '../../commons/hosts';
import validate from "../../device/components/validators/device-validators";

class DeviceForm extends React.Component {
    constructor(props) {
        super(props);
        this.reloadHandler = this.props.reloadHandler;
        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: false,
            users: [],
            selectedUserId: this.props.selectedDevice ? this.props.selectedDevice.userId : '',
            formControls: {
                description: {
                    value: this.props.selectedDevice ? this.props.selectedDevice.description : '',
                    placeholder: 'Enter description...',
                    valid: false,
                    touched: false,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                maxHEC: {
                    value: this.props.selectedDevice ? this.props.selectedDevice.maxHEC : '',
                    placeholder: 'Max hourly energy consumption...',
                    valid: false,
                    touched: false,
                },
                address: {
                    value: this.props.selectedDevice ? this.props.selectedDevice.address : '',
                    placeholder: 'Enter address...',
                    valid: false,
                    touched: false,
                },
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleChange(event) {
        const name = event.target.name;
        const value = event.target.value;

        const updatedControls = { ...this.state.formControls };
        const updatedFormElement = updatedControls[name];

        updatedFormElement.value = value;
        updatedFormElement.touched = true;
        updatedFormElement.valid = validate(value, updatedFormElement.validationRules);
        updatedControls[name] = updatedFormElement;

        let formIsValid = true;
        for (let updatedFormElementName in updatedControls) {
            formIsValid = updatedControls[updatedFormElementName].valid && formIsValid;
        }

        this.setState({
            formControls: updatedControls,
            formIsValid: formIsValid
        });
    }

    handleSubmit() {
        const device = {
            id: this.props.selectedDevice ? this.props.selectedDevice.id : null,
            description: this.state.formControls.description.value,
            maxHEC: this.state.formControls.maxHEC.value,
            address: this.state.formControls.address.value,
            userId: this.state.selectedUserId || null
        };

        if (this.props.selectedDevice) {
            this.updateDevice(device);
        } else {
            this.addDevice(device);
        }
    }

    updateDevice(device) {
        API_USERS.updateDevice(device, (result, status, error) => {
            if (status === 200 || status === 201) {
                this.props.reloadHandler();
                this.props.toggleForm();
            } else {
                console.error("[X] Error updating device:", error);
            }
        });
    }

    addDevice(device) {
        API_USERS.addDevice(device, (result, status, error) => {
            if (status === 200 || status === 201) {
                this.props.reloadHandler();
                this.props.toggleForm();
            } else {
                console.error("[X] Error adding device:", error);
            }
        });
    }

    handleDelete() {
        if (!this.props.selectedDevice) return; // nu sterge daca nu am cv selectat
        
        if (window.confirm("Are you sure you want to delete this device?")) {
            API_USERS.deleteDevice(this.props.selectedDevice.id, (result, status, error) => {
                if (status === 200 || status === 204) {
                    console.log(`Device ${this.props.selectedDevice.id} deleted successfully.`);
                    this.props.reloadHandler();
                    this.props.toggleForm(); 
                } else {
                    console.error("[X] Error deleting device:", error);
                }
            });
        }
    }

    componentDidMount() {
        fetch(HOST.device_backend_api + "/user_in_device")
            .then(res => res.json())
            .then((result) => {
                this.setState({ users: result });

                if (this.props.selectedDevice) {
                    this.setState({ selectedUserId: this.props.selectedDevice.userId || '' });
                }
            })
            .catch((error) => {
                console.error("[X] Error fetching users:", error);
            });
    }

    render() {
        return (
            <div>
                <FormGroup id='description'>
                    <Label for='descriptionField'> Description: </Label>
                    <Input
                        name='description'
                        id='descriptionField'
                        placeholder={this.state.formControls.description.placeholder}
                        onChange={this.handleChange}
                        value={this.state.formControls.description.value}
                        valid={this.state.formControls.description.valid}
                        required
                    />
                </FormGroup>

                <FormGroup id='address'>
                    <Label for='addressField'> Address: </Label>
                    <Input
                        name='address'
                        id='addressField'
                        placeholder={this.state.formControls.address.placeholder}
                        onChange={this.handleChange}
                        value={this.state.formControls.address.value}
                        valid={this.state.formControls.address.valid}
                        required
                    />
                </FormGroup>

                <FormGroup id='maxHEC'>
                    <Label for='maxHECField'> Maximum hourly energy consumption: </Label>
                    <Input
                        name='maxHEC'
                        id='maxHECField'
                        placeholder={this.state.formControls.maxHEC.placeholder}
                        type="number"
                        onChange={this.handleChange}
                        value={this.state.formControls.maxHEC.value}
                        valid={this.state.formControls.maxHEC.valid}
                        required
                    />
                </FormGroup>

                <FormGroup id='userSelect'>
                    <Label for='userSelectField'> Assign User:</Label>
                    <Input
                        type="select"
                        name="userSelect"
                        id="userSelectField"
                        value={this.state.selectedUserId}
                        onChange={(e) => this.setState({ selectedUserId: e.target.value })}
                    >
                        <option value="">Select User</option>
                        {this.state.users.map(user => (
                            <option key={user.id} value={user.id}>
                                {/* daca are 'name' atunci ii user din microserv de user
                                daca nu, ii din micros. de device deci ii din /user_in_service */}
                                {user.name ? `${user.name} (ID: ${user.id})` : `User ID: ${user.id}`}
                            </option>
                        ))}
                    </Input>
                </FormGroup>

                <FormGroup>
                    <Button color="primary" onClick={this.handleSubmit} disabled={!this.state.formIsValid}>
                        {this.props.selectedDevice ? 'Save Changes' : 'Add Device'}
                    </Button>

                    {this.props.selectedDevice && (
                        <Button color="danger" onClick={this.handleDelete} style={{ marginLeft: "10px" }}>
                            Delete Device
                        </Button>
                    )}
                </FormGroup>
            </div>
        );
    }
}

export default DeviceForm;
