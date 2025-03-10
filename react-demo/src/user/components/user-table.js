import React from "react";
import Table from "../../commons/tables/table";
import {Col, Row} from "reactstrap";
import Button from "react-bootstrap/Button";
import * as API_USERS from ".././api/user-api"

const columns = [
    {
        Header: 'Username',
        accessor: 'name',
    },
    {
        Header: 'Password',
        accessor: 'password',
    },
    {
        Header: 'Role',
        accessor: 'role',
    }
];

const filters = [
    { accessor: 'name' },
    { accessor: 'password' },
    { accessor: 'role' }
];

class UserTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedUser: null, 
        };
    }

    handleRowClick = (user) => {
        console.log("User clicked: ", user);
        this.setState({ selectedUser: user });
        if (this.props.onRowClick) {
            this.props.onRowClick(user);
        }
    };

    render() {
        const { tableData = [] } = this.props;
        const { selectedUser } = this.state;

        return (
            <div>
                <Table
                    data={tableData}
                    columns={columns}
                    search={filters}
                    pageSize={5}
                    getTrProps={this.handleRowClick}
                />

                
                {selectedUser && (
                    <div>
                        <h3>Edit User</h3>
                        <Row>
                            <Col sm="6">
                                <label>Username:</label>
                                <input
                                    type="text"
                                    value={selectedUser.name}
                                    onChange={(e) => this.handleInputChange('name', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <label>Password:</label>
                                <input
                                    type="text"
                                    value={selectedUser.password}
                                    onChange={(e) => this.handleInputChange('password', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <label>Role:</label>
                                <input
                                    type="text"
                                    value={selectedUser.role}
                                    onChange={(e) => this.handleInputChange('role', e.target.value)}
                                />
                            </Col>
                            <Col sm="6">
                                <button onClick={this.handleSave}>Save</button>
                                <button onClick={this.handleDelete}>Delete</button>
                            </Col>
                        </Row>
                    </div>
                )}
            </div>
        );
    }

    handleInputChange = (field, value) => {
        this.setState({
            selectedUser: {
                ...this.state.selectedUser,
                [field]: value
            }
        });
    };
    handleSave = () => {
        const updatedUser = this.state.selectedUser;  
        console.log("Saving user: ", updatedUser);
        API_USERS.updateUser(updatedUser, (result, status, err) => {
            if (result !== null && status === 200) {
                window.location.reload();
                this.setState({
                    tableData: result,  
                    isLoaded: true,
                });
            } else {
                this.setState({
                    errorStatus: status,
                    error: err
                });
            }
        });
    };
    

    handleDelete = () => {
        const userId = this.state.selectedUser.id;
        console.log("Deleting user with ID: ", userId);
        API_USERS.deleteUser({ id: userId }, (result, status, error) => {
            if (result !== null && status === 200) {
               window.location.reload();
               
            } else {
                this.setState({ errorStatus: status, error: error });
            }
        });
    };
    
    
}

export default UserTable;
