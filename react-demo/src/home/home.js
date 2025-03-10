import React from 'react';
import { withRouter } from 'react-router-dom';
import { Container, Table, Alert } from 'reactstrap';
import { HOST } from '../commons/hosts';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';


class Home extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            userName: '',
            userId: null,
            devices: [], 
            alerts: []  
        };
    }

    componentDidMount() {
        const userRole = localStorage.getItem('userRole');
        const userName = localStorage.getItem('userName');
        const userId = localStorage.getItem('userId');

        console.log("user id in home:", userId);

        if (!userRole || userRole !== 'client') {
            this.props.history.push('/login');
        } else {
            this.setState({ userName: userName, userId: userId }, () => {
                this.fetchDevices();
                this.setupWebSocket();  
            });
        }
    }

    fetchDevices = () => {

        const userId = localStorage.getItem('userId'); 

        if (!userId) {
            console.error("[X] User ID not found in localStorage.");
            return;
        }

       // fetch(`${HOST.device_backend_api}/device`) // un client poate sa vada toate device urile
        fetch(`${HOST.device_backend_api}/device/user/${userId}/deviceList`) // clientu vede numa lista lui
            .then(response => response.json())
            .then(data => {
                console.log("[OK] All Devices Fetched:", data);
                this.setState({ devices: data });
            })
            .catch(error => console.error("[X] Error fetching devices:", error));
    };

    setupWebSocket = () => {
        const socket = new SockJS('http://monitoring.localhost/ws');
       // const socket = new SockJS('http://localhost:8082/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            console.log("Connected to WebSocket!");

            stompClient.subscribe('/topic/alerts', (message) => {
                const alertMessage = message.body;
                console.log("Notification received: ", alertMessage);

                this.setState(prevState => ({
                    alerts: [...prevState.alerts, alertMessage] 
                }));
            });
        });
    };

    render() {
        console.log("Rendering Home. Devices:", this.state.devices);
        const { userName, devices, alerts } = this.state;

        return (
            <div>
                <Container fluid>
                    <h1 className="display-3">
                        Hello, {userName || 'Client'}!
                    </h1>
                </Container>

                <Container>
                    <h2>Alerts</h2>
                    {alerts.length === 0 ? (
                        <p>No alerts yet.</p>
                    ) : (
                        alerts.map((alert, index) => (
                            <Alert key={index} color="danger">
                                {alert}
                            </Alert>
                        ))
                    )}
                </Container>

                <Container>
                    <h2>All Devices</h2>
                    {devices.length === 0 ? (
                        <p>No devices found.</p>
                    ) : (
                        <Table striped>
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Description</th>
                                    <th>Address</th>
                                    <th>MaxHEC</th>
                                </tr>
                            </thead>
                            <tbody>
                                {devices.map((device, index) => (
                                    <tr key={device.id}>
                                        <td>{index + 1}</td>
                                        <td>{device.description}</td>
                                        <td>{device.address}</td>
                                        <td>{device.maxHEC}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Container>
            </div>
        );
    }
}

export default withRouter(Home);
