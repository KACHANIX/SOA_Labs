import React, {Component} from "react";
import {GetServerUrl, keyPressHandler} from "../utils/utils";

class DeleteOrganization extends Component {
    constructor(props) {
        super(props);
        this.delete = this.delete.bind(this);
    }

    delete(e){
        document.getElementById("deleteError").style.display = "none";
        document.getElementById("deleteSuccess").style.display = "none";

        var id = document.getElementById("idDelete").value;
        var parsedId = parseInt(id);

        if (isNaN(id)) {
            document.getElementById("deleteError").style.display = "block";
            return;
        }
        if (parsedId < 1  ) {
            document.getElementById("deleteError").style.display = "block";
            return;
        }
        var xhr = new XMLHttpRequest();
        var url = GetServerUrl()+'/' + id;
        xhr.open("DELETE", url);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("deleteSuccess").style.display = "block";
                    document.getElementById("deleteError").style.display = "none";
                    document.getElementById("deleteErrorId").style.display = "none";


                } else if (xhr.status == 422) {
                    document.getElementById("deleteSuccess").style.display = "none";
                    document.getElementById("deleteError").style.display = "block";
                    document.getElementById("deleteErrorId").style.display = "none";
                }
                else if (xhr.status == 404){
                    document.getElementById("deleteSuccess").style.display = "none";
                    document.getElementById("deleteError").style.display = "none";
                    document.getElementById("deleteErrorId").style.display = "block";
                }
            }
        }
        xhr.send();
    }

    render() {
        return (
            <div>
                <div className="extraBlock">
                    Delete organization by ID:
                    <br/>
                    <input id="idDelete" type="text"
                           // onKeyDown={(e)=>{keyPressHandler(e,'idDelete')}}
                           placeholder="id"/>
                    <br/>
                    <button onClick={this.delete}>Send request</button>
                    <br/>
                    <label id="deleteSuccess" className="success">Success!</label>
                    <label id="deleteError" className="error">Your input is incorrect!</label>
                    <label id="deleteErrorId" className="error">You're trying to delete a non-existing organization!</label>
                </div>
            </div>);
    }
}
export default DeleteOrganization;