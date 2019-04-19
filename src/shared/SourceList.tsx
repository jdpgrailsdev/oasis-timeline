import * as React from "react";
import SourceUtils from '../util/sourceUtils.js';
import TimelineData from '../data/timelineData.js';

export default class SourceList extends React.Component<any, any> {

    render() {
        return(
            <ul>
                {SourceUtils.generateSourceList(TimelineData.data)}
            </ul>
        );
    }
}