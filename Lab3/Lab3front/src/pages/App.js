import React, {Component} from 'react';
import '../styles/App.css';
import FindOrganizations from "./FindOrganizations";
import UpdateOrganization from "./UpdateOrganization";
import AddOrganization from "./AddOrganization";
import DeleteOrganization from "./DeleteOrganization";
import {BrowserRouter as Router, Route, Link, Switch} from 'react-router-dom';
import Extras from "./Extras";
import {acquiseAddress, fireAddress} from "../utils/utils";

class App extends Component {
    constructor(props) {
        super(props);
        this.fire = this.fire.bind(this);
        this.acquire = this.acquire.bind(this);
    }


    fire() {
        document.getElementById("orgIncorrectError").style.display = "none";
        document.getElementById("orgNotFoundError").style.display = "none";
        document.getElementById("orgFireSuccess").style.display = "none";

        var id = document.getElementById("orgId").value;
        var parsedId = parseInt(id);
        if (isNaN(parsedId)) {
            document.getElementById("orgIncorrectError").style.display = "block";
            return;
        }
        if (parsedId < 1) {
            document.getElementById("orgIncorrectError").style.display = "block";
            return;
        }
        var xhr = new XMLHttpRequest();
        var url = fireAddress() + id;
        xhr.open("POST", url);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                console.log(xhr.responseText == '');
                if (xhr.status == 200) {
                    document.getElementById("orgFireSuccess").style.display = "block";
                } else if (xhr.status == 404 && xhr.responseText == '') {
                    document.getElementById("orgNotFoundError").style.display = "block";
                } else if (xhr.status == 404 && xhr.responseText != '') {
                    document.getElementById("orgIncorrectError").style.display = "block";
                }
            }
        }
        xhr.send();
    }

    acquire() {
        document.getElementById("acqIncorrectError").style.display = "none";
        document.getElementById("acqNotFoundError").style.display = "none";
        document.getElementById("acquireSuccess").style.display = "none";

        var acquirerId = document.getElementById("acquirerId").value;
        var parsedAcquirerId = parseInt(acquirerId);
        var acquiredId = document.getElementById("acquiredId").value;
        var parsedAcquiredId = parseInt(acquiredId);

        if (isNaN(parsedAcquirerId) || isNaN(parsedAcquiredId)) {
            document.getElementById("acqIncorrectError").style.display = "block";
            return;
        }
        if (parsedAcquirerId < 1 || parsedAcquiredId < 1) {
            document.getElementById("acqIncorrectError").style.display = "block";
            return;
        }

        var xhr = new XMLHttpRequest();
        var url = acquiseAddress() + parsedAcquirerId + '/' + parsedAcquiredId;
        xhr.open("POST", url);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                console.log(xhr.responseText == '');
                if (xhr.status == 200) {
                    document.getElementById("acquireSuccess").style.display = "block";
                } else if (xhr.status == 404 && xhr.responseText == '') {
                    document.getElementById("acqNotFoundError").style.display = "block";
                } else if (xhr.status == 404 && xhr.responseText != '') {
                    document.getElementById("acqIncorrectError").style.display = "block";
                } else if (xhr.status == 400) {
                    document.getElementById("acqIncorrectError").style.display = "block";
                }
            }
        }
        xhr.send();
    }

    render() {
        return (
            <div className="App">
                <div id="lab-1">
                    <label>LAB 1</label>
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
                <div id="lab-2">
                    <hr/>
                    <label>LAB 2</label>
                    <div className="extraBlock">
                        <label htmlFor="orgId">Fire all organization's employees:</label><br/>
                        <input type="text" id="orgId" placeholder="ID"/><br/>
                        <button onClick={this.fire}>FIRE</button>
                        <label id="orgIncorrectError" className="error">Your input is incorrect</label>
                        <label id="orgNotFoundError" className="error">Your organization doesn't exist</label>
                        <label id="orgFireSuccess" className="success">Success!</label>
                    </div>
                    <div className="extraBlock">
                        <label>Acquire</label><br/>
                        <label htmlFor="acquirerId">Acquirer</label><input type="text" id="acquirerId"
                                                                           placeholder="ACQUIRER ID"/><br/>
                        <label htmlFor="acquiredId">Acquired</label><input type="text" id="acquiredId"
                                                                           placeholder="ACQUIRED ID"/><br/>
                        <button onClick={this.acquire}>ACQUIRE</button>
                        <label id="acqIncorrectError" className="error">Your input is incorrect</label>
                        <label id="acqNotFoundError" className="error">One of your organizations doesn't exist</label>
                        <label id="acquireSuccess" className="success">Success!</label>
                    </div>
                </div>


            </div>
        );
    }
}

export default App;
