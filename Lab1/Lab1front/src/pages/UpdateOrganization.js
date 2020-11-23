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

        console.log(id);
        console.log(name);
        console.log(y);
        console.log(annualTurnover);
        if (id == '' || name == '' | y == '' || annualTurnover == '') {
            document.getElementById("updateErrorValid").style.display = "block";
            return;
        }


        var x = document.getElementById("xUpdate").value;
        var street = document.getElementById("streetUpdate").value;
        var type = document.getElementById("typeUpdate").value;

        console.log(x);
        console.log(street);
        console.log(type);
        var url = GetServerUrl() + "/" + encodeURIComponent(id);
        var jsonStr = '{';
        var count = 0;
        if (x != '') {
            jsonStr += '"x":  ' + JSON.stringify(x)+'';
            count++;
        }
        if (street != '') {
            if (count != 0) {
                jsonStr += ','
            }
            // url = url + '&street=' + street;
            count++;
            jsonStr += '"street": ' + JSON.stringify(street) + '';

        }
        if (type != '') {
            if (count != 0) {
                jsonStr += ','
            }
            // url = url + '&type=' + type;
            count++;
            jsonStr += '"type": ' + JSON.stringify(type)+'';

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
            jsonStr += '"y": ' + JSON.stringify(y)+'';
            count++;
        }
        if (annualTurnover != '') {
            if (count != 0) {
                jsonStr += ','
            }
            jsonStr += '"turnover": ' + JSON.stringify(annualTurnover)+'';
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
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("updateSuccess").style.display = "block";
                    document.getElementById("updateErrorData").style.display = "none";
                    document.getElementById("updateErrorId").style.display = "none";
                } else if (xhr.status == 422) {
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
                       onKeyDown={textKeyPressHandler} placeholder="id"/>
                <br/>
                <input id="nameUpdate" type="text" placeholder="Name" className="necessary-field"
                       onKeyDown={textKeyPressHandler}/>
                <br/>
                <input id="xUpdate" type="text"
                       onKeyDown={textKeyPressHandler} placeholder="x"/>
                <br/>
                <input id="yUpdate" type="text" className="necessary-field"
                       onKeyDown={textKeyPressHandler} placeholder="y"/>
                <br/>
                <input id="turnoverUpdate" type="text" className="necessary-field"
                       onKeyDown={textKeyPressHandler} placeholder="Annual Turnover"/>
                <br/>
                <input id="streetUpdate" type="text" placeholder="Address" onKeyDown={textKeyPressHandler}/>
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