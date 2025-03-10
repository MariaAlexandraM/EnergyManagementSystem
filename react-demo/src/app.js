import React from 'react'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import NavigationBar from './navigation-bar'
import Home from './home/home';
import Login from './login';
import PersonContainer from './person/person-container'
import UserContainer from './user/user-container'

import ErrorPage from './commons/errorhandling/error-page';
import styles from './commons/styles/project-style.css';
import AdminDashboard from './user/admin-dashboard';
import DeviceContainer from './device/device-container';

class App extends React.Component {

    render() {

        localStorage.clear();
        return (
            <div className={styles.back}>
                <Router>
                    <div>
                        <NavigationBar />
                        <Switch>
                            <Route
                                exact
                                path='/'
                                render={() => <Login />}
                            />

                            <Route
                                exact
                                path='/login'
                                render={() => <Login />}
                            />

                            <Route
                                exact
                                path='/home'
                                render={() => <Home />}
                            />

                            <Route
                                exact
                                path='/person'
                                render={() => <PersonContainer />}
                            />

                            <Route
                                exact
                                path='/user'
                                render={() => <UserContainer />}
                            />

                            <Route
                                exact
                                path='/admin-dashboard'
                                render={() => <AdminDashboard />}
                            />

                            <Route
                                exact
                                path='/admin'
                                render={() => <AdminDashboard />}
                            />

                            <Route
                                exact
                                path='/device'
                                render={() => <DeviceContainer />}
                            />

                            {/*Error*/}
                            <Route
                                exact
                                path='/error'
                                render={() => <ErrorPage />}
                            />

                            <Route render={() => <ErrorPage />} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )
    };
}

export default App
