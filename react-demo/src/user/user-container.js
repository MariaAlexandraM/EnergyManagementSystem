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
import UserForm from "./components/user-form";
import * as API_USERS from "./api/user-api";
import UserTable from "./components/user-table";

class UserContainer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedUser: null,  // User to edit (if any)
            tableData: [],
            isLoaded: false,
            errorStatus: 0,
            error: null,
            showForm: false,  // Controls modal visibility
        };
    }

    componentDidMount() {
        this.fetchUsers();
    }

    fetchUsers() {
        return API_USERS.getUsers((result, status, err) => {
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
        // Prevent opening the form on row click
    };

    toggleForm = () => {
        this.setState({ 
            showForm: !this.state.showForm,
            selectedUser: null  // Reset selected user when form is toggled
        });
    };

    reload = () => {
        this.setState({ isLoaded: false });
        this.fetchUsers(); // Re-fetch users after adding/updating
    };

    render() {
        return (
            <div>
                <CardHeader>
                    <strong>User Management</strong>
                </CardHeader>
                <Card>
                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            <Button color="primary" onClick={this.toggleForm}>
                                {this.state.showForm ? "Cancel" : "Add User"}
                            </Button>
                        </Col>
                    </Row>

                    <Row>
                        <Col sm={{ size: '8', offset: 1 }}>
                            {this.state.isLoaded && (
                                <UserTable
                                    tableData={this.state.tableData}
                                    onRowClick={this.handleRowClick}  // Row click should not open the form
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

                    {/* Modal for User Form */}
                    <Modal isOpen={this.state.showForm} toggle={this.toggleForm} size="lg">
                        <ModalHeader toggle={this.toggleForm}>
                            {this.state.selectedUser ? "Edit User" : "Add User"}
                        </ModalHeader>
                        <ModalBody>
                            <UserForm
                                selectedUser={this.state.selectedUser}
                                reloadHandler={this.reload}  // Reload data after saving or updating
                                toggleForm={this.toggleForm}  // Close the form when done
                            />
                        </ModalBody>
                    </Modal>
                </Card>
            </div>
        );
    }
}

export default UserContainer;
