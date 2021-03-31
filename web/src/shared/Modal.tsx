import * as React from "react";

import TimelineData from '../data/timelineDataLoader.js';

export default class Modal extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            contextData: TimelineData.additionalContextData
        };
    }

    renderAdditionalContext() {
        if(this.props.type === 'recordings') {
            return <span><span>This session includes the recording of the following songs:</span><ul>{this.renderAdditionalContextList()}</ul></span>;
        } else if(this.props.type === 'gigs') {
            return <span><span>The set list includes the following songs:</span><ul>{this.renderAdditionalContextList()}</ul></span>;
        } else if(this.props.type === 'releases') {
            return <span><span>The track list includes:</span><ul>{this.renderAdditionalContextList()}</ul></span>
        } else if(this.props.type === 'noteworthy') {
            return <span><ul>{this.renderAdditionalContextList()}</ul></span>;
        } else {
            return "Information currently unavailable";
        }
    }

    renderAdditionalContextList() {
        const key = TimelineData.generateKey(this.props.timestamp, this.props.type);
        if(this.state.contextData.hasOwnProperty(key)) {
            return [...this.state.contextData[key]]
                .map((data, i) => this.renderContextData(data) );
        } else {
            return "Information currently unavailable";
        }
    }

    renderContextData(data:any) {
        const baseId = this.props.timestamp + "_" + data;
        return <li key={"key_" + baseId} id={baseId}><i id={baseId + "_i"}>{data}</i></li>;
    }

    render() {
        if (!this.props.show) {
          return(
              null
          );
        }
        return(
            <div className="modal" data-testid="modal-top-test">
                 { this.renderAdditionalContext() }
            </div>
        );
    }
}