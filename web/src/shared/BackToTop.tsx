import * as React from "react";
import { HashLink as Link } from "react-router-hash-link";

export default class BackToTop extends React.Component<any, any> {

    generateLink() {
        return this.props.baseUri + "#" + this.props.anchorId;
    }

    render() {
        return(
            <span className="backToTop" data-testid="back-to-top-test">
                <i className="material-icons md-18">arrow_upward</i>
                <div>
                    <Link to={this.generateLink()}>Back To Top</Link>
                </div>
            </span>
        );
    }
}