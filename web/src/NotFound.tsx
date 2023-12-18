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

export default class NotFound extends React.Component<any, any> {

    render() {
        return (
            <div className="main" id="top" data-testid="not-found-top-test">
                <h2>Oops! You seem to be caught beneath a landslide.</h2>
                <img src="images/landslide.png"
                     alt="Not Found"
                     className="notfound"
                     data-testid="not-found-test" />
                <div className="notFoundText">
                    Use the navigation menu bar above to return to the site!
                </div>
            </div>
        );
    }
}