import React, {Component} from "react";
import {GetServerUrl, keyPressHandler, textKeyPressHandler} from "../utils/utils";

class FindOrganizations extends Component {
    constructor(props) {
        super(props);
        this.getById = this.getById.bind(this);
        this.get = this.get.bind(this);

        this.state = {
            organizations: []
        };
    }

    getById() {
        document.getElementById("getIdIncorrectError").style.display = "none";
        document.getElementById("getIdSuccess").style.display = "none";
        document.getElementById("getIdError").style.display = "none";
        document.getElementById("getIdErrorId").style.display = "none";

        var id = (document.getElementById("idGet").value);

        if (id ==''){
            document.getElementById("getIdIncorrectError").style.display = "block";
            return;
        }

        var parsedId = parseInt(id);
        if (isNaN(id)) {
            document.getElementById("getIdIncorrectError").style.display = "block";
            return;
        }
        if (parsedId < 1  ) {
            document.getElementById("getIdIncorrectError").style.display = "block";
            return;
        }

        var xhr = new XMLHttpRequest();
        var url = GetServerUrl() + '/' + id
        xhr.open("GET", url);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("getIdSuccess").style.display = "block";
                    document.getElementById("getIdError").style.display = "none";
                    document.getElementById("getIdErrorId").style.display = "none";
                    console.log((JSON.parse(xhr.responseText)));
                    this.setState({
                        organizations: [(JSON.parse(xhr.responseText))]
                    });

                } else if (xhr.status == 400) {
                    document.getElementById("getIdSuccess").style.display = "none";
                    document.getElementById("getIdError").style.display = "block";
                    document.getElementById("getIdErrorId").style.display = "none";
                    this.setState({
                        organizations: []
                    });
                } else if (xhr.status == 404) {
                    document.getElementById("getIdSuccess").style.display = "none";
                    document.getElementById("getIdError").style.display = "none";
                    document.getElementById("getIdErrorId").style.display = "block";
                    this.setState({
                        organizations: []
                    });
                }
            }
        }
        xhr.send();

    }

    get() {
        document.getElementById('pageError').style.display = 'none';
        document.getElementById('filterError').style.display = 'none';
        var idFilter = document.getElementById("idFilter").value;
        var turnoverFilter = document.getElementById("turnoverFilter").value;
        var nameFilter = document.getElementById("nameFilter").value;
        var streetFilter = document.getElementById("streetFilter").value;
        var xFilter = document.getElementById("xFilter").value;
        var yFilter = document.getElementById("yFilter").value;
        var dateFilter = document.getElementById("dateFilter").value;
        var typeFilter = document.getElementById("typeFilter").value;
        var employeesFilter = document.getElementById("employeesFilter").value;

        var idSort = document.getElementById("idSort").value;
        var turnoverSort = document.getElementById("turnoverSort").value;
        var nameSort = document.getElementById("nameSort").value;
        var streetSort = document.getElementById("streetSort").value;
        var xSort = document.getElementById("xSort").value;
        var ySort = document.getElementById("ySort").value;
        var dateSort = document.getElementById("dateSort").value;
        var typeSort = document.getElementById("typeSort").value;
        var employeesSort = document.getElementById("employeesSort").value;

        var url = GetServerUrl();
        var filterCount = 0;
        {
            if (idFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (idFilter.includes(',') || idFilter.includes('!')|| idFilter.includes('/')|| idFilter.includes('#')|| idFilter.includes('&')|| idFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'id!' + (idFilter);
            }
            if (turnoverFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (turnoverFilter.includes(',') || turnoverFilter.includes('!')|| turnoverFilter.includes('/')|| turnoverFilter.includes('#')|| turnoverFilter.includes('&')|| turnoverFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'turnover!' + (turnoverFilter);
            }
            if (nameFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (nameFilter.includes(',') || nameFilter.includes('!')|| nameFilter.includes('/')|| nameFilter.includes('#')|| nameFilter.includes('&')|| nameFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'name!' + (nameFilter);
            }
            if (streetFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (streetFilter.includes(',') || streetFilter.includes('!')|| streetFilter.includes('/')|| streetFilter.includes('#')|| streetFilter.includes('&')|| streetFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'street!' + (streetFilter);
            }
            if (xFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (xFilter.includes(',') || xFilter.includes('!')|| xFilter.includes('/')|| xFilter.includes('#')|| xFilter.includes('&')|| xFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'x!' + (xFilter);
            }
            if (yFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (yFilter.includes(',') || yFilter.includes('!')|| yFilter.includes('/')|| yFilter.includes('#')|| yFilter.includes('&')|| yFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'y!' + (yFilter);
            }
            if (dateFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (dateFilter.includes(',') || dateFilter.includes('!')|| dateFilter.includes('/')|| dateFilter.includes('#')|| dateFilter.includes('&')|| dateFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'date!' + (dateFilter);
            }
            if (typeFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (typeFilter.includes(',') || typeFilter.includes('!')|| typeFilter.includes('/')|| typeFilter.includes('#')|| typeFilter.includes('&')|| typeFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'type!' + (typeFilter);
            }
            if (employeesFilter != '') {
                if (filterCount == 0) {
                    url += '?filterBy=';
                } else {
                    url += ',';
                }
                filterCount++;
                if (employeesFilter.includes(',') || employeesFilter.includes('!')|| employeesFilter.includes('/')|| employeesFilter.includes('#')|| employeesFilter.includes('&')|| employeesFilter.includes('?')){
                    document.getElementById('filterError').style.display = 'block';
                    return ;
                }
                url += 'employees!' + (employeesFilter);
            }
        }

        var sortCount = 0;
        {
            if (idSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'id!' + idSort;
            }
            if (turnoverSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'turnover!' + turnoverSort;
            }
            if (nameSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'name!' + nameSort;
            }
            if (streetSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'street!' + streetSort;
            }
            if (xSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'x!' + xSort;
            }
            if (ySort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'y!' + ySort;
            }
            if (dateSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }

                } else {
                    url += ',';
                }
                sortCount++;
                url += 'date!' + dateSort;
            }
            if (typeSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }
                } else {
                    url += ',';
                }
                sortCount++;
                url += 'type!' + typeSort;
            }
            if (employeesSort != '') {
                if (sortCount == 0) {
                    if (filterCount == 0) {
                        url += '?sortBy=';
                    } else {
                        url += '&sortBy=';
                    }
                } else {
                    url += ',';
                }
                sortCount++;
                url += 'employees!' + employeesSort;
            }
        }

        var sumCount = sortCount + filterCount;

        var sizePage = document.getElementById("sizePage").value;
        var pagePage = document.getElementById("pagePage").value;


        if (sizePage != '' && pagePage != '') {

            var parsedSize = parseFloat(sizePage);
            var parsedPage = parseFloat(pagePage);
            if (isNaN(parsedSize) || isNaN(parsedPage)) {
                document.getElementById("pageError").style.display = "block";
                return;
            }
            if (parsedSize < 1  || parsedPage < 0) {
                document.getElementById("pageError").style.display = "block";
                return;
            }

            url += sumCount > 0 ? '&page=' : '?page=';
            url += encodeURIComponent(sizePage) + '!' + encodeURIComponent(pagePage);
        } else if (sizePage != '' || pagePage != '') {
            document.getElementById('pageError').style.display = 'block';
            return;
        }


        var xhr = new XMLHttpRequest();
        xhr.open("GET", url);
        xhr.onload = (e) => {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    document.getElementById("getError").style.display = "none";
                    this.setState({
                        organizations: (JSON.parse(xhr.responseText))
                    });

                } else if (xhr.status == 400) {
                    document.getElementById("getError").style.display = "block";
                    this.setState({
                        organizations: []
                    });
                }
            }
        }
        xhr.send();
    }

    render() {
        const dust = {display: "inline-block", marginLeft: '5px'};
        return (
            <div>
                <div className="extraBlock">
                    Find organization by id:
                    <input id="idGet" type="text"
                        // onKeyDown={keyPressHandler}
                           placeholder="id"/>
                    <br/>
                    <button onClick={this.getById}>Send request</button>
                    <label id="getIdSuccess" className="success">Success!</label>
                    <label id="getIdError" className="error">Server received incorrect data!</label>
                    <label id="getIdIncorrectError" className="error">Your input is incorrect!</label>
                    <label id="getIdErrorId" className="error">You're trying to find a non-existing
                        organization!</label>
                </div>
                <div className="extraBlock">
                    <b>Find organizations:</b>
                    <br/>
                    Filters
                    <br/>
                    <input id="idFilter" type="text"
                        // onKeyDown={(e)=>{keyPressHandler(e,'idFilter')}}
                           placeholder="id" min="1"/>
                    <input id="turnoverFilter" type="text"
                        // onKeyDown={(e)=>{keyPressHandler(e,'turnoverFilter')}}
                           placeholder="Annual Turnover"/>
                    <br/>
                    <input id="nameFilter" type="text" placeholder="Name" />
                    <input id="streetFilter" type="text" placeholder="Street" />
                    <br/>
                    <input id="xFilter" type="text"
                        // onKeyDown={(e)=>{keyPressHandler(e,'xFilter')}}
                           placeholder="x"/>
                    <input id="yFilter" type="text"
                        // onKeyDown={(e)=>{keyPressHandler(e,'yFilter')}}
                           placeholder="y"/>
                    <br/>
                    <input id="dateFilter" type="text" placeholder="Creation Date" />
                    <input id="employeesFilter" type="text" placeholder="Employees"  />
                    <br/>
                    <select id="typeFilter">
                        <option value="" selected>Select Organization Type</option>
                        <option value={0}>COMMERCIAL</option>
                        <option value={1}>PUBLIC</option>
                        <option value={2}>GOVERNMENT</option>
                        <option value={3}>TRUST</option>
                        <option value={4}>OPEN_JOINT_STOCK_COMPANY</option>
                    </select>
                    <br/>
                    <br/>
                    Sort
                    <br/>
                    <div style={dust}>
                        Id
                        <br/>
                        <select id="idSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>

                    <div style={dust}>
                        Name
                        <br/>
                        <select id="nameSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Turnover
                        <br/>
                        <select id="turnoverSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Date
                        <br/>
                        <select id="dateSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        X
                        <br/>
                        <select id="xSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Y
                        <br/>
                        <select id="ySort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Street
                        <br/>
                        <select id="streetSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Type
                        <br/>
                        <select id="typeSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <div style={dust}>
                        Employees
                        <br/>
                        <select id="employeesSort">
                            <option value="" selected>Select</option>
                            <option value="ASC">ASC</option>
                            <option value="DESC">DESC</option>
                        </select>
                    </div>
                    <br/>
                    <br/>
                    Pagination
                    <br/>
                    <input id="sizePage" type="text"
                           // onKeyDown={textKeyPressHandler}
                           placeholder="Page Size" min="1"/>
                    <input id="pagePage" type="text"
                           // onKeyDown={textKeyPressHandler}
                           placeholder="Page Number"
                           min="0"/>

                    <label id="pageError" className="error">Set page size and page number both correctly!</label>
                    <label id="getError" className="error">Server received incorrect data!</label>
                    <label id="filterError" className="error">One of filters has incorrect data!</label>

                    <br/>
                    <button onClick={this.get}>Send request</button>

                </div>
                <div id="textBlock">
                    {this.state.organizations.map((e) => {
                        return <div className="organizationBlock">
                            Organization<br/>
                            Id: {e.id}<br/>
                            Name:{e.name}<br/>
                            Annual Turnover: {e.annualTurnover}<br/>
                            Coordinates: x {e.coordinates.x} y {e.coordinates.y}<br/>
                            Address :{e.postalAddress.street} <br/>
                            Creation date: {e.creationDate}<br/>
                            Organization type: {e.type}<br/>
                            Employees: {e.employees}
                        </div>
                    })}
                </div>
            </div>
        );
    }
}

export default FindOrganizations;