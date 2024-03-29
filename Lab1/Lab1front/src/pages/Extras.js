import React, {Component} from "react";
import '../styles/Extras.css';
import {GetServerUrl, keyPressHandler} from "../utils/utils";

class Extras extends Component {
    constructor(props) {
        super(props);
        this.delete = this.delete.bind(this);
        this.count = this.count.bind(this);
        this.average = this.average.bind(this);
    }

    delete() {
        var annualTurnover = encodeURIComponent(document.getElementById("turnoverDelete").value);
        var xhr = new XMLHttpRequest();
        var params = '?turnover=' + annualTurnover;
        // console.log(GetServerUrl() + 'equal-turnover' + params);
        xhr.open("DELETE", GetServerUrl() + '/equal-turnover' + params);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("deleteSuccess").style.display = "block";
                    document.getElementById("deleteError").style.display = "none";

                } else if (xhr.status == 422) {
                    document.getElementById("deleteSuccess").style.display = "none";
                    document.getElementById("deleteError").style.display = "block";
                }
            }
        }
        xhr.send();
    }

    count() {
        var annualTurnover = encodeURIComponent(document.getElementById("turnoverCount").value);

        var xhr = new XMLHttpRequest();
        var params = '?turnover=' + annualTurnover;
        xhr.open("GET", GetServerUrl() + '/higher-turnovers' + params);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("countSuccess").style.display = "block";
                    document.getElementById("countError").style.display = "none";
                    document.getElementById("countText").innerText = (JSON.parse(xhr.responseText)).Count;


                } else if (xhr.status == 422) {
                    document.getElementById("countSuccess").style.display = "none";
                    document.getElementById("countError").style.display = "block";
                    document.getElementById("countText").innerText = "";
                }
            }
        }
        xhr.send();
    }

    average() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", GetServerUrl() + '/average-turnover');
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("averageSuccess").style.display = "block";
                    document.getElementById("averageText").innerText = (JSON.parse(xhr.responseText)).AverageTurnover;
                }
            }
        }
        xhr.send();
    }


    render() {
        return (
            <div>
                <div className="extraBlock">
                    Delete all organizations whose Annual Turnover equals:
                    <br/>
                    <input id="turnoverDelete" type="text"
                           // onKeyDown={(e)=>{keyPressHandler(e,'turnoverDelete')}}
                           placeholder="AnnualTurnover"/>
                    <br/>
                    <button onClick={this.delete}>Send request</button>
                    <br/>
                    <label id="deleteSuccess" className="success">Success!</label>
                    <label id="deleteError" className="error">Server received incorrect data!</label>
                </div>
                <div className="extraBlock">
                    Get average Annual Turnover: <b id="averageText"/>
                    <br/>
                    <button onClick={this.average}>Send request</button>
                    <label id="averageSuccess" className="success">Success!</label>
                </div>
                <div className="extraBlock">
                    Count organizations with higher Annual Turnover:
                    <br/>
                    <input id="turnoverCount" type="text"
                           // onKeyDown={(e)=>{keyPressHandler(e,'turnoverCount')}}
                           placeholder="AnnualTurnover"/>
                    <br/>
                    <button onClick={this.count}>Send request</button>
                    <br/>
                    <br/>
                    Organizations : <b id="countText"/>
                    <br/>
                    <label id="countSuccess" className="success">Success!</label>
                    <label id="countError" className="error">Server received incorrect data!</label>
                </div>
            </div>
        );
    }
}

export default Extras;