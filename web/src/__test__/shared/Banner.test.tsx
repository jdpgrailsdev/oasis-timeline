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
import {render,screen} from "@testing-library/react";
import '@testing-library/jest-dom';
import React from "react";
import Banner from "../../shared/Banner";

describe('banner tests', () => {

    test('test rendering the banner component', () => {
        render(<Banner />);

        const content = screen.getAllByTestId("banner-test");
        const banner = content.pop();
        expect(banner).toBeDefined();
        if (typeof banner !== 'undefined') {
            const logo = banner.querySelector("img");
            expect(logo).toHaveAttribute("src", "images/header.png");
        } else {
            fail('Banner DIV object is undefined.');
        }
    });
});