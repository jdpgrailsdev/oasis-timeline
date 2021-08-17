/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";

export default class Collapsible extends React.Component<any, any> {
    constructor(props: any){
        super(props);
        this.state = {
            open: false
        }
        this.togglePanel = this.togglePanel.bind(this);
    }

    togglePanel(e:any){
        this.setState({open: !this.state.open})
    }

    render() {
        const testId = this.props.title.replaceAll(" ", "-").toLowerCase();
        return (<div data-testid={"collapsible-" + testId + "-test"}>
            <div onClick={(e)=>this.togglePanel(e)}
                 className="researchHeader" data-testid={"collapsible-" + testId + "-header-test"}>
                <span className="researchHeaderTitle">{this.props.title}</span>
            </div>
        {this.state.open ? (
            <div className="researchSection" data-testid={"collapsible-" + testId + "-body-test"}>
                {this.props.children}
            </div>
        ) : null}
        </div>);
    }
}