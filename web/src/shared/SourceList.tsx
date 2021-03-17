import * as React from "react";
import SourceUtils from '../util/sourceUtils.js';
import TimelineData from '../data/timelineDataLoader.js';

export default class SourceList extends React.Component<any, any> {

    render() {
        return(
            <ul data-testid="source-list-test">
                {SourceUtils.generateSourceList(TimelineData.data)}
            </ul>
        );
    }
}