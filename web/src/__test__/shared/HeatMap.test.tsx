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
import { fireEvent, render, screen } from "@testing-library/react";
import '@testing-library/jest-dom';
import HeatMap from "../../shared/HeatMap";

describe('heat map tests', () => {

    beforeAll(() => {

    });

    afterAll(() => {

    });

    test('test that the heatmap can be rendered using the timeline data', () => {
        let selectedDate: Date = new Date();
        let callback = (newState:any) => { selectedDate = newState.selectedDate;};
        let cellSize = 17;
        let cellPadding = 0.5;
        let rowWidthFactor = 6;
        let monthWidth = ((cellSize + cellPadding) * 7) + cellSize;

        render(
            <HeatMap
                callback={callback}
                cellSize={cellSize}
                cellPadding={cellPadding}
                height={cellSize * 20}
                monthsPerRow={rowWidthFactor}
                monthWidth={monthWidth}
                width={monthWidth * (rowWidthFactor + 0.2)} />
        );

        const content = screen.getAllByTestId("event-heatmap-test");
        const heatMap = content.pop();
        expect(heatMap).toBeDefined();

        if (typeof heatMap !== 'undefined') {
            const container = heatMap.querySelector('heatmap-container-test')
            expect(container).toBeDefined();
            const calendar = heatMap.querySelector('[data-testid="heatmap-calendar-container-test"]');
            expect(calendar).toBeDefined();

            if (calendar !== null) {
                const january = calendar.querySelector('[data-testid="heatmap-month-january-container-test"]');
                expect(january).toBeDefined();
                if (january !== null) {
                    const januaryText = january.querySelector('text');
                    expect(januaryText).toHaveTextContent('Jan');
                    expect(januaryText).toHaveAttribute('x', "0");
                    expect(januaryText).toHaveAttribute('y', "-5");
                } else {
                    fail('January text DIV object is null.');
                }

                const july = calendar.querySelector('[data-testid="heatmap-month-july-container-test"]');
                expect(july).toBeDefined();
                if(july !== null) {
                    const julyText = july.querySelector('text');
                    expect(julyText).toHaveTextContent('Jul');
                    expect(julyText).toHaveAttribute('x', "0");
                    expect(julyText).toHaveAttribute('y', "153");

                    const day = july.querySelector('[data-testid="heatmap-day-6-12-container-test"]');
                    expect(day).toBeDefined();

                    if (day !== null) {
                        fireEvent.click(day, new MouseEvent('click', {}));
                        expect(selectedDate.getUTCMonth()).toBe(6);
                        expect(selectedDate.getUTCDate()).toBe(12);
                    } else {
                        fail('Day DIV object is null.');
                    }
                } else {
                    fail('July text DIV object is null.');
                }
            } else {
                fail('Calendar DIV object is null.');
            }
        } else {
            fail('Heat Map DIV object is undefined.');
        }

    });

    test('test that the heatmap can be rendered using the timeline data for a mobile client', () => {
        let selectedDate: Date = new Date();
        let callback = (newState:any) => { selectedDate = newState.selectedDate;};
        let cellSize = 17;
        let cellPadding = 0.5;
        let rowWidthFactor = 3;
        let monthWidth = ((cellSize + cellPadding) * 7) + cellSize;

        render(
            <HeatMap
                callback={callback}
                cellSize={cellSize}
                cellPadding={cellPadding}
                height={cellSize * 37}
                monthsPerRow={rowWidthFactor}
                monthWidth={monthWidth}
                width={monthWidth * (rowWidthFactor + 0.2)} />
        );

        const content = screen.getAllByTestId("event-heatmap-test");
        const heatMap = content.pop();
        expect(heatMap).toBeDefined();

        if (typeof heatMap !== 'undefined') {
            const container = heatMap.querySelector('heatmap-container-test')
            expect(container).toBeDefined();
            const calendar = heatMap.querySelector('[data-testid="heatmap-calendar-container-test"]');
            expect(calendar).toBeDefined();

            if (calendar !== null) {
                const january = calendar.querySelector('[data-testid="heatmap-month-january-container-test"]');
                expect(january).toBeDefined();
                if (january !== null) {
                    const januaryText = january.querySelector('text');
                    expect(januaryText).toHaveTextContent('Jan');
                    expect(januaryText).toHaveAttribute('x', "0");
                    expect(januaryText).toHaveAttribute('y', "-5");
                } else {
                    fail('January text DIV object is null.');
                }

                const july = calendar.querySelector('[data-testid="heatmap-month-july-container-test"]');
                expect(july).toBeDefined();
                if(july !== null) {
                    const julyText = july.querySelector('text');
                    expect(julyText).toHaveTextContent('Jul');
                    expect(julyText).toHaveAttribute('x', "0");
                    expect(julyText).toHaveAttribute('y', "306");

                    const day = july.querySelector('[data-testid="heatmap-day-6-12-container-test"]');
                    expect(day).toBeDefined();
                    if (day !== null) {
                        fireEvent.click(day, new MouseEvent('click', {}));
                        expect(selectedDate.getUTCMonth()).toBe(6);
                        expect(selectedDate.getUTCDate()).toBe(12);
                    } else {
                        fail('Day DIV object is null.');
                    }
                } else {
                    fail('July text DIV object is null.');
                }
            } else {
                fail('Calendar DIV object is null.');
            }
        } else {
            fail('Heat Map DIV object is undefined.');
        }

    });
});