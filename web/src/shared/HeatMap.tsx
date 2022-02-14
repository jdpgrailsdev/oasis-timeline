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

const MONTH_Y_ORIGIN = -5;
const ROW_HEIGHT_FACTOR = 9;    //e.g. 1 header row, 5 weeks of rows plus some spacing
const POINTER_CURSOR_STYLE = "cursor: pointer;"
const SELECTED_DATE_STYLE = "outline: thin solid black;";
const TRANSFORM_X = 20.5;
const TRANSFORM_Y = 40.0;

/**
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

    /**
     * Converts a month name to a three letter abbreviation.
     * @param month The month name to abbreviate.
     * @return The abbreviated month name.
     */
    abbreviateMonth(month:String) {
        return month.substring(0,3);
    };

    /**
     * Determines a date's week of month assignment.
     * @param d The date/event count tuple struct.
     * @return The week of month that contains the provided date.
     */
    computeWeek(d:{date: Date, eventCount: number}) {
        return (d3.utcSunday.count(
            new Date(d.date.getUTCFullYear(), d.date.getUTCMonth(), 1), d.date));
    }

    /**
     * Formats a date into MMM dd for tooltip use.
     * @param d The date to be formatted for a tooltip.
     * @return The date as a string for use in a tooltip.
     */
    formatDate(d:Date) {
        const month = d.toLocaleString('default',
            { timeZone: 'UTC', month: 'long'});
        const day = d.getUTCDate();
        return month + " " + day;
    };

    /**
     * Converts the string name of a month into its numerical value
     * @param d The date/event count tuple struct.
     * @return A Date object representing the month in the tuple.
     */
    getMonthOfDate(d:[string, {date: Date, eventCount: number}[]]) {
        const currentYear = new Date().getUTCFullYear();
        return (new Date(Date.parse(d[0] + " 1, " + currentYear)).getUTCMonth());
    }

    componentDidMount() {
        const state = this.createHeatMap()
        this.generate(state);
        this.setState(state);
    }

    componentDidUpdate() {
        this.generate(this.state);
        this.props.callback(this.state);
    }

    shouldComponentUpdate(nextProps: Readonly<any>, nextState: Readonly<any>, nextContext: any) {
        // Only update if the selected date changed.  Ignore all other state updates
        return nextState.selectedDate !== this.state.selectedDate;
    }

    /**
     * Computes the starting Y axis position for a container based on the
     * provided information.
     * @param extractMonth A function that returns the month associated with the component.
     * @param defaultPosition The default position for the Y-axis.
     * @param transformer A function that generates the Y-axis position if the default
     *  position is not selected.
     * @return The Y-axis value for the given component.
     */
    computeOriginYPosition(extractMonth:() => number, defaultPosition:number,
                           transformer:(factor:number) => number) {
        const month:number = extractMonth() + 1;
        const factor:number = Math.ceil(month/this.props.monthsPerRow);
        return factor === 1 ? defaultPosition : transformer(factor);
    }

    /**
     * Generates the color selector function based on the provided data.
     * @param data The date/event count tuple struct.
     * @return The color selector function.
     */
    createColorSelector(data: [string, {date: Date, eventCount: number}[]][]) {
        const max_value = d3.max(data.map(m => m[1]), d => {
            return typeof d !== 'undefined' ? d[0].eventCount : 0;
        }) as number

        return d3.scaleSequential(d3.interpolatePiYG).domain([-max_value, +max_value]);
    }

    /**
     * Creates the data for the heat map based on the existing timeline data.
     * @return The heat map data grouped by month.
     */
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

        return d3.groups(events, e => e.date.toLocaleString('default',
            { timeZone: 'UTC', month: 'long'}));
    }

    /**
     * Creates the heat map container.
     * @return A state map with the generated heat map components for use by other methods.
     */
    createHeatMap() {
        const data = this.createData();
        const colorSelector = this.createColorSelector(data);

        const container = this.generateContainer(this.props.height, this.props.width);
        const calendar = this.generateCalendar(container, TRANSFORM_X, TRANSFORM_Y);
        const month = this.generateMonth(calendar, data);

        // Label the months
        month.append("text")
            .attr("x", d => ((this.getMonthOfDate(d) % this.props.monthsPerRow) *
                this.props.monthWidth))
            .attr("y", d => {
                return this.computeOriginYPosition(
                    () => (this.getMonthOfDate(d)),
                    MONTH_Y_ORIGIN,
                f => (this.props.cellSize * ROW_HEIGHT_FACTOR) * (f - 1))
            })
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

    /**
     * Generates the contents of the heat map.
     * @param state A state map containing the components of the heat map.
     */
    generate(state:any) {
        // Add the heat map data to each month
        state.months
            .selectAll("rect")
            .data(([, values]:[string, {date: Date, eventCount: number}[]]) => values.values())
            .join("rect")
            .attr("width", this.props.cellSize - 1)
            .attr("height", this.props.cellSize - 1)
            .attr("x", (d:{date: Date, eventCount: number}) =>  this.positionDateXAxis(d))
            .attr("y", (d:{date: Date, eventCount: number}) => this.positionDateYAxis(d))
            .attr("fill", (d:{date: Date, eventCount: number}) => state.colorSelector(d.eventCount))
            .attr("date", (d:{date: Date, eventCount: number}) => d.date.toUTCString())
            .attr("style", (d:{date: Date, eventCount: number}) => this.selectStyle(d))
            .attr("data-testid", (d:{date: Date, eventCount: number}) => {
                return "heatmap-day-" + d.date.getUTCMonth() + "-" +
                    d.date.getUTCDate() + "-container-test";
            })
            .on('click', (e:MouseEvent, d:{date:Date, eventCount:number}) => {
                this.selectDate(d.date);
            })
            .append("title")
            .text((e:{date: Date, eventCount: number}) => this.generateTooltip(e));
    }

    /**
     * Generates the heat map container component.
     * @param height The height of the component.
     * @param width The width of the component.
     * @return The heat map container component.
     */
    generateContainer(height:number, width:number) {
        return d3.select('#event-heatmap')
            .append('svg')
            .attr('class', 'svg')
            .attr('data-testid', 'heatmap-container-test')
            .attr('width', width)
            .attr('height', height)
            .attr("font-family", "helvetica")
            .attr("font-size", 12);
    }

    /**
     * Generates the calendar component that contains the heat map for each month.
     * @param svg The heat map container component.
     * @param transformX The X coordinate of the component.
     * @param transformY The Y coordinate of the component.
     */
    generateCalendar(svg:d3.Selection<SVGSVGElement, unknown, HTMLElement, any>,
                     transformX:number, transformY:number) {
        return svg.selectAll("g")
            .data([1])  // Placeholder data so that the container generates
            .join("g")
            .attr("transform", `translate(${transformX},${transformY})`)
            .attr("data-testid", "heatmap-calendar-container-test");
    }

    /**
     * Generates a month component that will be placed on the heat map component.
     * @param calendar The calendar component.
     * @param months The list of months (with data) to add to the calendar.
     * @return The container with each month and its data.
     */
    generateMonth(calendar: d3.Selection<d3.BaseType | SVGGElement, number, SVGSVGElement, unknown>,
                   months: [string, {date: Date, eventCount: number}[]][]) {
        return calendar.append("g")
            .selectAll("g")
            .data(months)
            .join("g")
            .attr("data-testid", (d:[string, {date: Date, eventCount: number}[]]) => {
                return "heatmap-month-" + d[0].toLowerCase() + "-container-test";
            });
    }

    /**
     * Generates the tooltip text to be displayed on each rectangle in the heat map.
     * @param e The date/event count tuple.
     * @return The tooltip text for the date/event count tuple.
     */
    generateTooltip(e:{date: Date, eventCount: number}) {
        return e.eventCount + " event(s) on " + this.formatDate(e.date)
    }

    /**
     * Computes the Y-axis coordinate on the heat map for a given date.
     * @param d The date/event count tuple.
     * @return The Y-axis coordinate for the given date.
     */
    positionDateYAxis(d:{date: Date, eventCount: number}) {
        const yOrigin = this.computeOriginYPosition(
            () => (d.date.getUTCMonth()),
            this.props.cellSize,
            f => (this.props.cellSize * ROW_HEIGHT_FACTOR + this.props.cellSize) * (f - 1))
        return (yOrigin + this.computeWeek(d) * (this.props.cellSize + this.props.cellPadding));
    }

    /**
     * Computes the X-axis coordinate on the heat map for a given date.
     * @param d The date/event count tuple.
     * @return The X-axis coordinate for the given date.
     */
    positionDateXAxis(d:{date: Date, eventCount: number}) {
        return ((d.date.getUTCDay() * (this.props.cellSize + this.props.cellPadding)) +
            ((d.date.getUTCMonth() % this.props.monthsPerRow) * this.props.monthWidth));
    }

    /**
     * On click method that handles the selection of a rectangle on the heat map,
     * extracts the date associated with that component and updates the state of this
     * component.
     * @param date The selected date.
     */
    selectDate(date:Date) {
        if(date !== null) {
            this.setState({
                selectedDate: date
            });
        }
    }

    /**
     * Generates the style to be applied to a given rectangle in the heat map based on
     * whether or not the date is the currently selected date.
     * @param d The date/event count tuple.
     * @return The style to be applied to the component.
     */
    selectStyle(d:{date: Date, eventCount: number}) {
        const style = (this.state.selectedDate !== null &&
            this.state.selectedDate.getUTCMonth() === d.date.getUTCMonth() &&
            this.state.selectedDate.getUTCDate() === d.date.getUTCDate())
            ? SELECTED_DATE_STYLE : "";
        return (style + " " + POINTER_CURSOR_STYLE).trim();
    }

    render() {
        return (
            <div id="event-heatmap"
                 className="eventHeatMap"
                 data-testid="event-heatmap-test">
            </div>
        )
    }
}