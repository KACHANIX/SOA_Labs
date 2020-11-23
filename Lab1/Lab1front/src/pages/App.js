import React, {Component} from 'react';
import '../styles/App.css';
import FindOrganizations from "./FindOrganizations";
import UpdateOrganization from "./UpdateOrganization";
import AddOrganization from "./AddOrganization";
import DeleteOrganization from "./DeleteOrganization";
import {BrowserRouter as Router, Route, Link, Switch} from 'react-router-dom';
import Extras from "./Extras";

class App extends Component {
    render() {
        return (
            <div className="App">
                <div id="linksBlock">
                    <Link to="/find">Find organization</Link>
                    <Link className="link" to="/update">Update organization</Link>
                    <Link className="link" to="/add">Add organization</Link>
                    <Link className="link" to="/delete">Delete organization</Link>
                    <Link className="link" to="/extras">Extra</Link>
                </div>
                <Switch>
                    <Route path="/find" component={FindOrganizations}></Route>
                    <Route path="/update" component={UpdateOrganization}></Route>
                    <Route path="/add" component={AddOrganization}></Route>
                    <Route path="/delete" component={DeleteOrganization}></Route>
                    <Route path="/extras" component={Extras}></Route>
                </Switch>
            </div>
        );
    }
}

export default App;
