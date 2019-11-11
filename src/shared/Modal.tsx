import * as React from "react";

import AdditionalContextData from '../data/additionalContextData.js';

export default class Modal extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            contextData: AdditionalContextData.data
        };
    }

    renderAdditionalContext() {
        if(this.props.type === 'recordings') {
            return <span><span>This session included the recording of the following songs:</span><ul>{this.renderAdditionalContextList()}</ul></span>;
        } else if(this.props.type === 'gigs') {
            return <span><span>The setlist included the following songs:</span><ul>{this.renderAdditionalContextList()}</ul></span>;
        } else {
            return "Information currently unavailable";
        }
    }

    renderAdditionalContextList() {
        const key = AdditionalContextData.generateKey(this.props.timestamp, this.props.type);
        if(this.state.contextData.hasOwnProperty(key)) {
            return [...this.state.contextData[key]]
                .map((data, i) => this.renderContextData(data) );
        } else {
            return "Information currently unavailable";
        }
    }

    renderContextData(data:any) {
        const baseId = this.props.timestamp + "_" + data;
        return <li id={baseId}><i id={baseId + "_i"}>{data}</i></li>;
    }

    render() {
        if (!this.props.show) {
          return(
              null
          );
        }
        return(
            <div className="modal">
                 { this.renderAdditionalContext() }
             </div>
        );
    }
}