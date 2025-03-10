import React, { Component } from 'react';
import { Button, Container } from 'reactstrap';
import { withRouter } from 'react-router-dom';

class AdminDashboard extends Component {
    componentDidMount() {
        const userRole = localStorage.getItem('userRole');
        
        if (!userRole || userRole !== 'admin') {
            this.props.history.push('/login');
        }
    }

    render() {
        return (
            <Container className="mt-5">
                <h2>Admin Dashboard</h2>
                <p>Welcome, Admin!</p>
                <Button color="primary" onClick={() => this.props.history.push('/user')} className="m-2">
                    Go to Users
                </Button>
                <Button color="secondary" onClick={() => this.props.history.push('/device')} className="m-2">
                    Go to Devices
                </Button>
            </Container>
        );
    }
}

export default withRouter(AdminDashboard);
