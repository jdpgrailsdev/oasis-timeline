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
import {fireEvent, render} from "@testing-library/react";
import Collapsible from "../../shared/Collapsible";
import {screen} from "@testing-library/dom";

describe('collapsible tests', () => {

    test('test rendering the collapsible component', () => {
        render(
            <Collapsible title="Test Section">
                <div>This is a test collapsible section.</div>
            </Collapsible>
        );
        const content = screen.getAllByTestId("collapsible-test-section-test");
        const collapsible = content.pop();
        expect(collapsible).toBeDefined();
        const header = screen.getByTestId("collapsible-test-section-header-test");
        expect(header.textContent).toBe("Test Section");
        const body = screen.queryByTestId("collapsible-test-section-body-test");
        expect(body).toBe(null);

        fireEvent.click(header);
        const expandedBody = screen.getByTestId("collapsible-test-section-body-test");
        expect(expandedBody.textContent).toBe("This is a test collapsible section.");

    })

});