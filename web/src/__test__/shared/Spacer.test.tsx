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
import React from "react";
import { screen } from "@testing-library/dom";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import Spacer from "../../shared/Spacer";

describe('spacer tests', () => {

    test('test rendering the spacer component', () => {
        render(
            <Spacer />
        );

        const content = screen.getAllByTestId("spacer-test");
        const wrapper = content.pop();
        expect(wrapper).toBeDefined();
        const spacer = wrapper.querySelector('div');
        expect(spacer).toHaveAttribute('style', 'border-bottom: 3px solid black; display: block; margin: 0px auto 10px auto; width: 65%;');
    });
});