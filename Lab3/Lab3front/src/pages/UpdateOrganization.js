import React, {Component} from "react";
import {GetServerUrl, keyPressHandler, textKeyPressHandler} from "../utils/utils";

class UpdateOrganization extends Component {
    constructor(props) {
        super(props);
        this.update = this.update.bind(this);
    }

    update(e) {
        document.getElementById("updateSuccess").style.display = "none";
        document.getElementById("updateErrorData").style.display = "none";
        document.getElementById("updateErrorId").style.display = "none";
        document.getElementById("updateErrorValid").style.display = "none";


        var id = document.getElementById("idUpdate").value;
        var name = document.getElementById("nameUpdate").value;
        var y = document.getElementById("yUpdate").value;
        var annualTurnover = document.getElementById("turnoverUpdate").value;
        var x = document.getElementById("xUpdate").value;

        console.log(id);
        console.log(name);
        console.log(y);
        console.log(x);
        console.log(annualTurnover);
        if (id == '' || name == '' | y == '' || annualTurnover == '' || x == '') {
            document.getElementById("updateErrorValid").style.display = "block";
            return;
        }


        var street = document.getElementById("streetUpdate").value;
        var type = document.getElementById("typeUpdate").value;
        var employees = document.getElementById("employeesUpdate").value;

        console.log(x);
        console.log(street);
        console.log(type);
        var url = GetServerUrl() + "/" + encodeURIComponent(id);
        var jsonStr = '{';
        var count = 0;
        if (x != '') {
            jsonStr += '"x":  ' + JSON.stringify(x) + '';
            count++;
        }
        if (street != '') {
            if (count != 0) {
                jsonStr += ','
            }
            count++;
            jsonStr += '"street": ' + JSON.stringify(street) + '';

        }
        if (type != '') {
            if (count != 0) {
                jsonStr += ','
            }
            // url = url + '&type=' + type;
            count++;
            jsonStr += '"type": ' + JSON.stringify(type) + '';

        }
        if (name != '') {
            if (count != 0) {
                jsonStr += ','
            }
            jsonStr += '"name": ' + JSON.stringify(name) + '';
            count++;

        }
        if (y != '') {
            if (count != 0) {
                jsonStr += ','
            }
            jsonStr += '"y": ' + JSON.stringify(y) + '';
            count++;
        }
        if (annualTurnover != '') {
            if (count != 0) {
                jsonStr += ','
            }
            jsonStr += '"turnover": ' + JSON.stringify(annualTurnover) + '';
        }
        if (employees != '') {
            if (count != 0) {
                jsonStr += ','
            }
            jsonStr += '"employees": ' + JSON.stringify(employees) + '';
        }


        jsonStr += '}'

        console.log(jsonStr);
        console.log(JSON.parse(jsonStr))

        if (count == 0) {
            document.getElementById("updateErrorValid").style.display = "block";
            return;
        } else {
            document.getElementById("updateErrorValid").style.display = "none";
        }


        var xhr = new XMLHttpRequest();
        xhr.open("PUT", url);
        xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("updateSuccess").style.display = "block";
                    document.getElementById("updateErrorData").style.display = "none";
                    document.getElementById("updateErrorId").style.display = "none";
                } else if (xhr.status == 400) {
                    document.getElementById("updateSuccess").style.display = "none";
                    document.getElementById("updateErrorData").style.display = "block";
                    document.getElementById("updateErrorId").style.display = "none";
                } else if (xhr.status == 404) {
                    document.getElementById("updateSuccess").style.display = "none";
                    document.getElementById("updateErrorData").style.display = "none";
                    document.getElementById("updateErrorId").style.display = "block";
                }
            }
        }
        xhr.send(jsonStr);
    }


    render() {
        return (
            <div className="extraBlock">
                <b>Update Organization's info</b>
                <br/>
                <input id="idUpdate" type="text" className="necessary-field"
                       placeholder="id"/>
                <br/>
                <input id="nameUpdate" type="text" placeholder="Name" className="necessary-field"
                />
                <br/>
                <input id="xUpdate" type="text" className="necessary-field"
                       placeholder="x"/>
                <br/>
                <input id="yUpdate" type="text" className="necessary-field"
                       placeholder="y"/>
                <br/>
                <input id="turnoverUpdate" type="text" className="necessary-field"
                       placeholder="Annual Turnover"/>
                <br/>
                <input id="streetUpdate" type="text" placeholder="Address"
                />
                <br/>
                <input id="employeesUpdate" type="text" placeholder="Employees"
                />
                <br/>
                <select id="typeUpdate">
                    <option value="" selected>Select one</option>
                    <option value={0}>COMMERCIAL</option>
                    <option value={1}>PUBLIC</option>
                    <option value={2}>GOVERNMENT</option>
                    <option value={3}>TRUST</option>
                    <option value={4}>OPEN_JOINT_STOCK_COMPANY</option>
                </select>
                <br/>

                <button onClick={this.update}>Send request</button>
                <br/>
                <label id="updateSuccess" className="success">Success!</label>
                <label id="updateErrorData" className="error">Server received incorrect data!</label>
                <label id="updateErrorId" className="error">You're trying to update a non-existing organization!</label>
                <label id="updateErrorValid" className="error">Fill in necessary fields correctly!</label>
            </div>);
    }
}

export default UpdateOrganization