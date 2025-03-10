import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label, Alert } from 'reactstrap';
import * as API_USERS from './user/api/user-api';

const Login = () => {
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const history = useHistory();

    const handleLogin = (e) => {
        e.preventDefault();

        API_USERS.login({ name, password }, (result, status, err) => {
            if (status === 200 && result) {
                const userRole = result.role;  
                const userName = result.name;
                const userId = result.id;

                localStorage.setItem('userRole', userRole);
                localStorage.setItem('userName', userName); 
                localStorage.setItem('userId', userId); 

                if (userRole === 'admin') {
                    console.log('Admin');
                    history.push('/admin-dashboard');
                } else if (userRole === 'client') {
                    console.log('Client');
                    history.push('/home');
                } else {
                    setError('Unexpected role received');
                }
            } else {
                setError('Invalid credentials');
            }
        });
    };

    return (
        <Container className="mt-5">
            <h2>Login</h2>
            {error && <Alert color="danger">{error}</Alert>}
            <Form onSubmit={handleLogin}>
                <FormGroup>
                    <Label for="name">Username</Label>
                    <Input
                        type="text"
                        name="name"
                        id="name"
                        placeholder="Enter your username"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </FormGroup>
                <FormGroup>
                    <Label for="password">Password</Label>
                    <Input
                        type="password"
                        name="password"
                        id="password"
                        placeholder="Enter your password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </FormGroup>
                <Button color="primary" type="submit">Login</Button>
            </Form>
        </Container>
    );
};

export default Login;
