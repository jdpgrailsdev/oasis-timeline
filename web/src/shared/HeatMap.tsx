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
import * as d3 from "d3";
import TimelineData from '../data/timelineDataLoader';

const CELL_SIZE = 15;
const CELL_PADDING = 0.5;
const HEIGHT = CELL_SIZE * 20;  // Account for two rows of months of up to 5 weeks each
const MONTH_Y_ORIGIN = -5;
const ROW_HEIGHT_FACTOR = 8;    //e.g. 1 header row, 5 weeks of rows plus some spacing
const ROW_PADDING = CELL_SIZE;
const ROW_WIDTH_FACTOR = 6;     //e.g. 6 months of cells
const TRANSFORM_X = 20.5;
const TRANSFORM_Y = 40.0;
const MONTH_WIDTH = ((CELL_SIZE + CELL_PADDING) * 7) + ROW_PADDING; // Month width = 7 days of cells plus one additional for padding
const WIDTH = MONTH_WIDTH * 6.2; // 6 months + some padding

/*
 * Custom heat-map style calendar component based on
 * https://observablehq.com/@d3/calendar-view.
 */
export default class HeatMap extends React.Component<any, any> {

    constructor(props:any) {
        super(props)

        this.state = {
            selectedDate: new Date(),
            calendar: null,
            container: null
        }

        this.selectDate = this.selectDate.bind(this);
    }

    abbreviateMonth(month:String) {
        return month.substring(0,3);
    };

    /*
     * Determines a date's week of month assignment.
     */
    computeWeek(d:{date: Date, eventCount: number}) {
        return (d3.utcSunday.count(
            new Date(d.date.getUTCFullYear(), d.date.getUTCMonth(), 1), d.date));
    }

    /*
     * Formats a date into MMM dd for tooltip use.
     */
    formatDate(d:Date) {
        const month = d.toLocaleString('default',
            { timeZone: 'UTC', month: 'long'});
        const day = d.getUTCDate();
        return month + " " + day;
    };

    /*
     * Converts the string name of a month into its numerical value
     */
    getMonthOfDate(d:[string, {date: Date, eventCount: number}[]]) {
        const currentYear = new Date().getUTCFullYear();
        return (new Date(Date.parse(d + " 1, " + currentYear)).getUTCMonth());
    }

    componentDidMount() {
        console.log("Selected date = " + this.state.selectedDate);
        const state = this.createHeatMap()
        this.generate(state);
        this.setState(state);
    }

    componentDidUpdate() {
        console.log("Selected date is now " + this.state.selectedDate.toUTCString() + ".  Updating...");
        this.generate(this.state);
    }

    shouldComponentUpdate(nextProps: Readonly<any>, nextState: Readonly<any>, nextContext: any) {
        return nextState.selectedDate !== this.state.selectedDate;
    }

    createColorSelector(data: [string, {date: Date, eventCount: number}[]][]) {
        const max_value = d3.max(data.map(m => m[1]), d => {
            return typeof d !== 'undefined' ? d[0].eventCount : 0;
        }) as number

        return d3.scaleSequential(d3.interpolatePiYG).domain([-max_value, +max_value]);
    }

    createData() {
        const currentYear = new Date().getUTCFullYear();
        const start = new Date(Date.UTC(currentYear, 0, 1));
        const end = new Date(Date.UTC(currentYear + 1, 0, 1));

        // Get all days in the year
        const utcDays = d3.utcDays(start, end);

        // Build a list of days -> events for each day
        const events = utcDays.map(u => {
            return {
                date: u,
                eventCount: TimelineData.generateHistory(
                    u.toLocaleString('default',
                        { timeZone: 'UTC', month: 'long', day: 'numeric'})
                ).length as number
            }
        });

        const months = d3.groups(events, e => e.date.toLocaleString('default',
            { timeZone: 'UTC', month: 'long'}));

        return months;
    }

    createHeatMap() {
        const data = this.createData();
        const colorSelector = this.createColorSelector(data);

        const container = this.generateContainer(HEIGHT, WIDTH);
        const calendar = this.generateCalendar(container, TRANSFORM_X, TRANSFORM_Y);
        const month = this.generateMonth(calendar, data);

        // Label the months
        month.append("text")
            .attr("x", d => ((this.getMonthOfDate(d) % ROW_WIDTH_FACTOR) * MONTH_WIDTH))
            .attr("y", d => this.getMonthOfDate(d) < ROW_WIDTH_FACTOR
                ? MONTH_Y_ORIGIN : (CELL_SIZE * ROW_HEIGHT_FACTOR))
            .text(([month]) => this.abbreviateMonth(month));

        const months = month.append("g");

        return {
            colorSelector: colorSelector,
            container: container,
            calendar: calendar,
            data: data,
            month: month,
            months: months
        };
    }

    generate(state:any) {
        // Add the heat map data to each month
        state.months
            .selectAll("rect")
            .data(([, values]:[string, {date: Date, eventCount: number}[]]) => values.values())
            .join("rect")
            .attr("width", CELL_SIZE - 1)
            .attr("height", CELL_SIZE - 1)
            .attr("x", (d:{date: Date, eventCount: number}) =>  this.positionDateXAxis(d))
            .attr("y", (d:{date: Date, eventCount: number}) => this.positionDateYAxis(d))
            .attr("fill", (d:{date: Date, eventCount: number}) => state.colorSelector(d.eventCount))
            .attr("date", (d:{date: Date, eventCount: number}) => d.date.toUTCString())
            .attr("style", (d:{date: Date, eventCount: number}) => this.selectStyle(d))
            .on('click', this.selectDate)
            .append("title")
            .text((e:{date: Date, eventCount: number}) => this.generateTooltip(e));
    }

    generateContainer(height:number, width:number) {
        return d3.select('#event-calendar')
            .append('svg')
            .attr('class', 'svg')
            .attr('width', width)
            .attr('height', height)
            .attr("font-family", "helvetica")
            .attr("font-size", 10);
    }

    generateCalendar(svg:d3.Selection<SVGSVGElement, unknown, HTMLElement, any>, transformX:number, transformY:number) {
        return svg.selectAll("g")
            .data([1])  // Placeholder data so that the container generates
            .join("g")
            .attr("transform", `translate(${transformX},${transformY})`);
    }

    generateMonth(calendar: d3.Selection<d3.BaseType | SVGGElement, number, SVGSVGElement, unknown>,
                   months: [string, {date: Date, eventCount: number}[]][]) {
        return calendar.append("g")
            .selectAll("g")
            .data(months)
            .join("g");
    }

    generateTooltip(e:{date: Date, eventCount: number}) {
        return e.eventCount + " event(s) on " + this.formatDate(e.date)
    }

    positionDateYAxis(d:{date: Date, eventCount: number}) {
        const yOrigin = d.date.getUTCMonth() < ROW_WIDTH_FACTOR ?
            0 : (CELL_SIZE * (ROW_HEIGHT_FACTOR + 1));
        return (yOrigin + this.computeWeek(d) * (CELL_SIZE + CELL_PADDING));
    }

    positionDateXAxis(d:{date: Date, eventCount: number}) {
        return ((d.date.getUTCDay() * (CELL_SIZE + CELL_PADDING)) +
            ((d.date.getUTCMonth() % ROW_WIDTH_FACTOR) * MONTH_WIDTH));
    }

    selectDate(e:any) {
        const dateAttr = e.path[0].attributes.getNamedItem('date');
        if(dateAttr !== null) {
            this.setState({
                selectedDate: new Date(Date.parse(dateAttr.value))
            });
        }
    }

    selectStyle(d:{date: Date, eventCount: number}) {
        return (this.state.selectedDate !== null &&
            this.state.selectedDate.getUTCMonth() === d.date.getUTCMonth() &&
            this.state.selectedDate.getUTCDate() === d.date.getUTCDate())
            ? "outline: thin solid rgb(39, 100, 25);" : "";
    }

    render() {
        return (
            <div id="event-calendar" className="eventCalendar" data-testid="event-calendar-test"></div>
        )
    }
}