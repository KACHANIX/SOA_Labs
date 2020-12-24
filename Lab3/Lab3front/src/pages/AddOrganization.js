import React, {Component} from "react";
import {GetServerUrl, keyPressHandler, textKeyPressHandler} from "../utils/utils";

class AddOrganization extends Component {
    constructor(props) {
        super(props);
        this.add = this.add.bind(this);
    }

    add(e) {
        document.getElementById("addSuccess").style.display = "none";

        var name = document.getElementById("nameAdd").value;
        var y = document.getElementById("yAdd").value;
        var annualTurnover = document.getElementById("turnoverAdd").value;
        var x = document.getElementById("xAdd").value;

        console.log(JSON.stringify(name))

        if (name == "" || y == "" || annualTurnover == "" || x == "") {
            document.getElementById("addErrorValid").style.display = "block";
            return;
        } else {
            document.getElementById("addSuccess").style.display = "none";
            document.getElementById("addErrorValid").style.display = "none";
        }

        var street = document.getElementById("streetAdd").value;
        var type = document.getElementById("typeAdd").value;
        var employees = document.getElementById("employeesAdd").value;
        // var url = GetServerUrl() + '?name=' + name + '&turnover=' + annualTurnover + '&y=' + y;
        var jsonStr = '{"name": ' + JSON.stringify(name) + ', "turnover": ' + JSON.stringify(annualTurnover) + ',"y": ' + JSON.stringify(y) + ', "x": ' + JSON.stringify(x);

        if (street != '') {
            jsonStr = jsonStr + ',"street": ' + JSON.stringify(street);
        }
        if (type != '') {
            jsonStr = jsonStr + ',"type": ' + JSON.stringify(type);
        }
        if (employees != '') {
            jsonStr = jsonStr + ',"employees": ' + JSON.stringify(employees);
        }

        jsonStr += '}';
        console.log(jsonStr);
        var body = JSON.parse(jsonStr);
        console.log(body);
        var xhr = new XMLHttpRequest();

        xhr.open("POST", GetServerUrl());

        xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
        xhr.setRequestHeader('Accept', '*/*');
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("addSuccess").style.display = "block";
                    document.getElementById("addErrorData").style.display = "none";

                } else if (xhr.status == 400) {
                    document.getElementById("addSuccess").style.display = "none";
                    document.getElementById("addErrorData").style.display = "block";
                }
            }
        }
        xhr.send(jsonStr);
    }


    render() {
        return (
            <div>
                <div className="extraBlock">
                    <b>Add Organization</b>
                    <br/>
                    <input id="nameAdd" type="text" className="necessary-field" placeholder="Name"
                        // onKeyDown={textKeyPressHandler}
                    />
                    <br/>
                    <input id="xAdd" type="text"  className="necessary-field"
                        // onKeyDown={(e)=>{keyPressHandler(e,'xAdd')}}
                           placeholder="x"/>
                    <br/>
                    <input id="yAdd" type="text" className="necessary-field"
                        // onKeyDown={(e)=>{keyPressHandler(e,'yAdd')}}
                           placeholder="y"/>
                    <br/>
                    <input id="turnoverAdd" type="TEXT" className="necessary-field"
                        // onKeyDown={(e)=>{keyPressHandler(e,'turnoverAdd')}}
                           placeholder="Annual Turnover"/>
                    <br/>
                    <input id="streetAdd" type="text" placeholder="Address"
                        // onKeyDown={textKeyPressHandler}
                    />
                    <br/>
                    <input id="employeesAdd" type="text" placeholder="Employee"
                        // onKeyDown={textKeyPressHandler}
                    />
                    <br/>

                    <select id="typeAdd">
                        <option value="" disabled selected>Select one</option>
                        <option value={0}>COMMERCIAL</option>
                        <option value={1}>PUBLIC</option>
                        <option value={2}>GOVERNMENT</option>
                        <option value={3}>TRUST</option>
                        <option value={4}>OPEN_JOINT_STOCK_COMPANY</option>
                    </select>
                    <br/>

                    <button onClick={this.add}>Send request</button>
                    <br/>
                    <label id="addSuccess" className="success">Success!</label>
                    <label id="addErrorData" className="error">Server received incorrect data!</label>
                    <label id="addErrorValid" className="error">Fill in necessary fields correctly!</label>
                </div>
            </div>);
    }
}

export default AddOrganization;