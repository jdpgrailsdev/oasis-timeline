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
import TimelineFilters from "./TimelineFilters";
import TimelineNavigation from "./TimelineNavigation";

export default class TimelineFilterGroup extends React.Component<any, any> {

    constructor(props: any) {
        super(props);

        this.toggleGroup = this.toggleGroup.bind(this);
    }

    toggleGroup() {
        const filtersActiveState = !this.props.filtersActive;
        const navigationActiveState = !this.props.navigationActive;
        this.props.onChange({ filtersActive: filtersActiveState, navigationActive: navigationActiveState });
    }

    render() {
      let visible = this.props.isVisible ? { display : 'block' } : { display : 'none' };

      return(
          <div className="filterGroup">
            <button className="filterButton" type="button" onClick={this.toggleGroup}>
                <i className="material-icons md-14">filter_list</i>
            </button>
            <div style={visible}>
                <div className="menuSpacer"></div>
                <TimelineNavigation navigationActive={this.props.navigationActive}/>
                <div className="menuSpacer"></div>
                <TimelineFilters filters={this.props.filters} filtersActive={this.props.filtersActive} onChange={this.props.onChange} onReset={this.props.onReset}/>
                <div className="menuSpacer"></div>
            </div>
        </div>
      )
    }
}