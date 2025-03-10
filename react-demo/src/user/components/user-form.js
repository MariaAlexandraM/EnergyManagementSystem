import React from 'react';
import Button from "react-bootstrap/Button";
import * as API_USERS from "../api/user-api";
import APIResponseErrorMessage from "../../commons/errorhandling/api-response-error-message";
import { Col, Row } from "reactstrap";
import { FormGroup, Input, Label } from 'reactstrap';
import validate from "../../user/components/validators/user-validators";

class UserForm extends React.Component {
    constructor(props) {
        super(props);
        this.reloadHandler = this.props.reloadHandler;
        this.state = {
            errorStatus: 0,
            error: null,
            formIsValid: false,
            formControls: {
                name: {
                    value: this.props.selectedUser ? this.props.selectedUser.name : '',
                    placeholder: 'Username',
                    valid: false,
                    touched: false,
                    validationRules: {
                        minLength: 3,
                        isRequired: true
                    }
                },
                password: {
                    value: this.props.selectedUser ? this.props.selectedUser.password : '',
                    placeholder: 'Password',
                    valid: false,
                    touched: false,
                },
                role: {
                    value: this.props.selectedUser ? this.props.selectedUser.role : '',
                    placeholder: 'admin OR client',
                    valid: false,
                    touched: false,
                },
            }
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
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
        const user = {
            name: this.state.formControls.name.value,
            password: this.state.formControls.password.value,
            role: this.state.formControls.role.value
        };

        if (this.props.selectedUser) {
            this.updateUser(user);
        } else {
            this.addUser(user);
        }
    }

    addUser(user) {
        return API_USERS.addUser(user, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                this.reloadHandler();
                this.props.toggleForm(); 
            } else {
                this.setState({
                    errorStatus: status,
                    error: error
                });
            }
        });
    }

    updateUser(user) {
        return API_USERS.updateUser(user, (result, status, error) => {
            if (result !== null && (status === 200 || status === 201)) {
                this.reloadHandler();
                this.props.toggleForm(); 
            } else {
                this.setState({
                    errorStatus: status,
                    error: error
                });
            }
        });
    }

    render() {
        return (
            <div>
                 <FormGroup id='name'>
                    <Label for='nameField'> Username: </Label>
                    <Input
                        name='name'
                        id='nameField'
                        placeholder={this.state.formControls.name.placeholder}
                        onChange={this.handleChange}
                        value={this.state.formControls.name.value}
                        valid={this.state.formControls.name.valid}
                        required
                    />
                </FormGroup>

                <FormGroup id='password'>
                    <Label for='passwordField'> Password: </Label>
                    <Input
                        name='password'
                        id='passwordField'
                        placeholder={this.state.formControls.password.placeholder}
                        onChange={this.handleChange}
                        value={this.state.formControls.password.value}
                        valid={this.state.formControls.password.valid}
                        required
                    />
                </FormGroup>

                <FormGroup id='role'>
    <Label for='roleField'> Role: </Label>
    <Input
        type="select"
        name='role'
        id='roleField'
        onChange={this.handleChange}
        value={this.state.formControls.role.value}
        valid={this.state.formControls.role.valid}
        required
    >
        <option value="" disabled>
            Select Role
        </option>
        <option value="admin">admin</option>
        <option value="client">client</option>
    </Input>
</FormGroup>


                <FormGroup>
                    <Button color="primary" onClick={this.handleSubmit} disabled={!this.state.formIsValid}>
                        {this.props.selectedUser ? 'Save Changes' : 'Add User'}
                    </Button>
                </FormGroup>
            </div>
        );
    }
}

export default UserForm;
